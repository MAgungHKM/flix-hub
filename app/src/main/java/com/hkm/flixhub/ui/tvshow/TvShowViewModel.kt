package com.hkm.flixhub.ui.tvshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity

class TvShowViewModel(private val showRepository: ShowRepository) : ViewModel() {

    fun getTvShows(): LiveData<ArrayList<ShowEntity>> = showRepository.getAllTvShows()
}