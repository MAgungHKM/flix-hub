package com.hkm.flixhub.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity

class MovieViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var movies = MutableLiveData<ArrayList<ShowEntity>>()

    fun getMovies(): LiveData<ArrayList<ShowEntity>> {
        if (movies.value.isNullOrEmpty())
            loadMovies()

        return movies
    }

    private fun loadMovies() {
        showRepository.getAllMovies().observeForever {
            movies.postValue(it)
        }
    }
}