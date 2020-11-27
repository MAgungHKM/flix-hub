package com.hkm.flixhub.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity

class MovieViewModel(private val showRepository: ShowRepository) : ViewModel() {

    fun getMovies(): LiveData<ArrayList<ShowEntity>> = showRepository.getAllMovies()
}