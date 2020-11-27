package com.hkm.flixhub.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.local.entity.DetailShowEntity

class DetailViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private lateinit var showId: String
    private lateinit var showType: String

    fun setSelectedShow(showId: String) {
        this.showId = showId
    }

    fun setSelectedShowType(showType: String) {
        this.showType = showType
    }

    fun getShowDetail(): LiveData<DetailShowEntity> {
        return when (showType) {
            DetailFragment.TYPE_MOVIE -> showRepository.getMovieDetail(showId)
            DetailFragment.TYPE_TV_SHOW -> showRepository.getTvShowDetail(showId)
            else -> {
                val errorCode = 420
                val statusCode = 69
                val statusMessage = "The type of entertainment you requested could not be found"
                val errorMessage =
                    "Request Error $errorCode, with status code: $statusCode. $statusMessage"
                val empty = MutableLiveData<DetailShowEntity>()
                val detail = DetailShowEntity(
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    errorMessage
                )

                empty.postValue(detail)

                return empty
            }
        }
    }
}