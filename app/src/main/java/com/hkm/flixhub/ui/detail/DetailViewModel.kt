package com.hkm.flixhub.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.vo.Resource
import com.hkm.flixhub.vo.Status

class DetailViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private lateinit var showId: String
    private lateinit var showType: String
    private var detail: LiveData<Resource<ShowEntity>>? = null

    fun setSelectedShow(showId: String) {
        this.showId = showId
    }

    fun setSelectedShowType(showType: String) {
        this.showType = showType
    }

    fun getShowDetail(): LiveData<Resource<ShowEntity>> {
        return when (showType) {
            ShowType.TYPE_MOVIE -> {
                if (detail == null || detail?.value == null)
                    detail = showRepository.getMovieDetail(showId)

                detail as LiveData<Resource<ShowEntity>>
            }
            ShowType.TYPE_TV_SHOW -> {
                if (detail == null || detail?.value == null)
                    detail = showRepository.getTvShowDetail(showId)

                detail as LiveData<Resource<ShowEntity>>
            }
            else -> {
                if (detail == null || detail?.value == null) {
                    val errorCode = 420
                    val statusCode = 69
                    val statusMessage = "The type of entertainment you requested could not be found"
                    val errorMessage =
                        "Request Error $errorCode, with status code: $statusCode. $statusMessage"
                    val show = ShowEntity(
                        showId = "null",
                        type = "null",
                        title = "null",
                        posterPath = "null",
                        errorMessage = errorMessage
                    )

                    val showResource = Resource(Status.SUCCESS, show, null)
                    val showLiveData = MutableLiveData<Resource<ShowEntity>>()
                    showLiveData.value = showResource

                    detail = showLiveData
                }

                detail as LiveData<Resource<ShowEntity>>
            }
        }
    }

    fun setFavorite(): Boolean {
        var state: Boolean? = null
        if (detail != null) {
            val showResource = detail?.value
            if (showResource != null) {
                val showEntity = showResource.data
                val newState = !(showEntity?.favorited as Boolean)
                showRepository.setShowFavorite(showEntity, newState)

                state = newState
            }
        }

        return state as Boolean
    }
}