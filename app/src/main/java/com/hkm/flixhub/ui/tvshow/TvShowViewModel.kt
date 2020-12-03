package com.hkm.flixhub.ui.tvshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity

class TvShowViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var tvShows = MutableLiveData<ArrayList<ShowEntity>>()

    fun getTvShows(): LiveData<ArrayList<ShowEntity>> {
        if (tvShows.value.isNullOrEmpty())
            loadMovies()

        return tvShows
    }

    private fun loadMovies() {
        showRepository.getAllTvShows().observeForever {
            tvShows.postValue(it)
        }
    }
}