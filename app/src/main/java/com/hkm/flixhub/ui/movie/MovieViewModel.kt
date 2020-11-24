package com.hkm.flixhub.ui.movie

import androidx.lifecycle.ViewModel
import com.hkm.flixhub.data.ShowEntity
import com.hkm.flixhub.utils.DataDummy

class MovieViewModel : ViewModel() {

    fun getMovies(): ArrayList<ShowEntity> = DataDummy.generateDummyMovies()
}