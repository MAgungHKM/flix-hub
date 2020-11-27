package com.hkm.flixhub.data.source

import androidx.lifecycle.LiveData
import com.hkm.flixhub.data.source.local.entity.DetailShowEntity
import com.hkm.flixhub.data.source.local.entity.ShowEntity

interface ShowRepository {
    fun getAllMovies(): LiveData<ArrayList<ShowEntity>>

    fun getAllTvShows(): LiveData<ArrayList<ShowEntity>>

    fun getMovieDetail(showId: String): LiveData<DetailShowEntity>

    fun getTvShowDetail(showId: String): LiveData<DetailShowEntity>
}