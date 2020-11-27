package com.hkm.flixhub.data.source

import com.hkm.flixhub.data.source.local.entity.DetailShowEntity
import com.hkm.flixhub.data.source.local.entity.ShowEntity

interface FakeShowRepository {
    fun getAllMovies(): ArrayList<ShowEntity>

    fun getAllTvShows(): ArrayList<ShowEntity>

    fun getMovieDetail(showId: String): ArrayList<DetailShowEntity>

    fun getTvShowDetail(showId: String): ArrayList<DetailShowEntity>
}