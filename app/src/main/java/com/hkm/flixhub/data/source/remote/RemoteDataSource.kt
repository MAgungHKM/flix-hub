package com.hkm.flixhub.data.source.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hkm.flixhub.R
import com.hkm.flixhub.api.ApiConfig
import com.hkm.flixhub.data.source.remote.response.*
import com.hkm.flixhub.utils.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource constructor(private val context: Context) {

    companion object {
        private val TAG = RemoteDataSource::class.java.simpleName
    }

    fun getAllMovie(page: String): LiveData<ApiResponse<MovieDiscoveryResponse>> {
        EspressoIdlingResource.increment()
        val movieDiscoveryResponse = MutableLiveData<ApiResponse<MovieDiscoveryResponse>>()
        val client = ApiConfig.getApiService().getMovieDiscovery(page = page)
        client.enqueue(object : Callback<MovieDiscoveryResponse> {
            override fun onResponse(
                call: Call<MovieDiscoveryResponse>,
                response: Response<MovieDiscoveryResponse>,
            ) {
                if (response.isSuccessful)
                    movieDiscoveryResponse.value =
                        ApiResponse.success(response.body() as MovieDiscoveryResponse)
                else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    val errorDiscoveryResponse = MovieDiscoveryResponse(
                        listOf(),
                        errorResponse.statusMessage,
                        errorResponse.statusCode,
                        errorResponse.success)
                    movieDiscoveryResponse.value = ApiResponse.error(msg, errorDiscoveryResponse)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<MovieDiscoveryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
        return movieDiscoveryResponse
    }

    fun getAllTvShow(page: String): LiveData<ApiResponse<TvShowDiscoveryResponse>> {
        EspressoIdlingResource.increment()
        val tvShowDiscoveryResponse = MutableLiveData<ApiResponse<TvShowDiscoveryResponse>>()
        val client = ApiConfig.getApiService().getTvShowDiscovery(page = page)
        client.enqueue(object : Callback<TvShowDiscoveryResponse> {
            override fun onResponse(
                call: Call<TvShowDiscoveryResponse>,
                response: Response<TvShowDiscoveryResponse>,
            ) {
                if (response.isSuccessful) {
                    tvShowDiscoveryResponse.value =
                        ApiResponse.success(response.body() as TvShowDiscoveryResponse)
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    val errorDiscoveryResponse = TvShowDiscoveryResponse(
                        listOf(),
                        errorResponse.statusMessage,
                        errorResponse.statusCode,
                        errorResponse.success)
                    tvShowDiscoveryResponse.value = ApiResponse.error(msg, errorDiscoveryResponse)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<TvShowDiscoveryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
        return tvShowDiscoveryResponse
    }

    fun getMovieDetail(id: String): LiveData<ApiResponse<MovieDetailResponse>> {
        EspressoIdlingResource.increment()
        val movieDetailResponse = MutableLiveData<ApiResponse<MovieDetailResponse>>()
        val client = ApiConfig.getApiService().getMovieDetail(id)
        client.enqueue(object : Callback<MovieDetailResponse> {
            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>,
            ) {
                if (response.isSuccessful) {
                    EspressoIdlingResource.increment()
                    val detailResponse = response.body() as MovieDetailResponse
                    val client2 = ApiConfig.getApiService().getMovieCredits(id)
                    client2.enqueue(object : Callback<MovieCreditsResponse> {
                        override fun onResponse(
                            call: Call<MovieCreditsResponse>,
                            response: Response<MovieCreditsResponse>,
                        ) {
                            if (response.isSuccessful) {
                                val list = (response.body() as MovieCreditsResponse).crew
                                detailResponse.crew = list
                                movieDetailResponse.value = ApiResponse.success(detailResponse)
                            } else {
                                val errorResponse =
                                    Gson().fromJson(response.errorBody()?.charStream(),
                                        ErrorResponse::class.java)
                                val msg = context
                                    .getString(R.string.error_message,
                                        response.code(),
                                        errorResponse.statusCode,
                                        errorResponse.statusMessage)
                                Log.e(TAG, msg)
                                val errorDetailResponse = MovieDetailResponse(
                                    "null",
                                    "null",
                                    "null",
                                    listOf(),
                                    -1.0,
                                    -1,
                                    "null",
                                    "null",
                                    "null",
                                    arrayListOf(),
                                    errorResponse.statusMessage,
                                    errorResponse.statusCode,
                                    errorResponse.success)
                                movieDetailResponse.value =
                                    ApiResponse.error(msg, errorDetailResponse)
                            }
                            EspressoIdlingResource.decrement()
                        }

                        override fun onFailure(call: Call<MovieCreditsResponse>, t: Throwable) {
                            Log.e(TAG, "onFailure: ${t.message.toString()}")
                            EspressoIdlingResource.decrement()
                        }
                    })
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    val errorDetailResponse = MovieDetailResponse(
                        "null",
                        "null",
                        "null",
                        listOf(),
                        -1.0,
                        -1,
                        "null",
                        "null",
                        "null",
                        arrayListOf(),
                        errorResponse.statusMessage,
                        errorResponse.statusCode,
                        errorResponse.success)
                    movieDetailResponse.value = ApiResponse.error(msg, errorDetailResponse)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
        return movieDetailResponse
    }

    fun getTvShowDetail(id: String): LiveData<ApiResponse<TvShowDetailResponse>> {
        EspressoIdlingResource.increment()
        val tvShowDetailResponse = MutableLiveData<ApiResponse<TvShowDetailResponse>>()
        val client = ApiConfig.getApiService().getTvShowDetail(id)
        client.enqueue(object : Callback<TvShowDetailResponse> {
            override fun onResponse(
                call: Call<TvShowDetailResponse>,
                response: Response<TvShowDetailResponse>,
            ) {
                if (response.isSuccessful)
                    tvShowDetailResponse.value =
                        ApiResponse.success(response.body() as TvShowDetailResponse)
                else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    val errorDetailResponse = TvShowDetailResponse(
                        "null",
                        "null",
                        "null",
                        listOf(),
                        -1.0,
                        "null",
                        "null",
                        -1,
                        listOf(),
                        "null",
                        errorResponse.statusMessage,
                        errorResponse.statusCode,
                        errorResponse.success)
                    tvShowDetailResponse.value = ApiResponse.error(msg, errorDetailResponse)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<TvShowDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
        return tvShowDetailResponse
    }
}