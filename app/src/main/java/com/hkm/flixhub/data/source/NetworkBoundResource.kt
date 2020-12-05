package com.hkm.flixhub.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.hkm.flixhub.data.source.remote.ApiResponse
import com.hkm.flixhub.data.source.remote.StatusResponse
import com.hkm.flixhub.utils.AppExecutors
import com.hkm.flixhub.vo.Resource

abstract class NetworkBoundResource<ResultType, RequestType>(private val mExecutors: AppExecutors) {

    constructor(mExecutors: AppExecutors, lastSort: String?) : this(mExecutors) {
        this.lastSort = lastSort
    }

    private var lastSort: String? = null
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)

        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            val lastSort = this.lastSort
            val shouldFetch =
                if (lastSort != null) shouldFetch(data, lastSort) else shouldFetch(data)

            if (shouldFetch) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    result.value = Resource.success(newData)
                }
            }
        }
    }

    private fun onFetchFailed() {}

    protected abstract fun loadFromDB(): LiveData<ResultType>

    protected abstract fun shouldFetch(data: ResultType?, lastSort: String? = null): Boolean

    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    protected abstract fun saveCallResult(data: RequestType, lastSort: String? = null)

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {

        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            result.value = Resource.loading(newData)
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response.status) {
                StatusResponse.SUCCESS ->
                    mExecutors.diskIO().execute {
                        if (lastSort != null)
                            saveCallResult(response.body, lastSort)
                        else
                            saveCallResult(response.body)

                        mExecutors.mainThread().execute {
                            result.addSource(loadFromDB()) { newData ->
                                result.value = Resource.success(newData)
                            }
                        }
                    }
                StatusResponse.EMPTY -> mExecutors.mainThread().execute {
                    result.addSource(loadFromDB()) { newData ->
                        result.value = Resource.success(newData)
                    }
                }
                StatusResponse.ERROR -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        result.value = Resource.error(response.message, newData)
                    }
                }
            }
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> = result
}