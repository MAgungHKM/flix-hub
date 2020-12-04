package com.hkm.flixhub.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.vo.Resource

interface ShowRepository {
    fun getAllMovies(page: String): LiveData<Resource<PagedList<ShowEntity>>>

    fun getAllTvShows(page: String): LiveData<Resource<PagedList<ShowEntity>>>

    fun getFavoritedMovies(page: String): LiveData<PagedList<ShowEntity>>

    fun getFavoritedTvShows(page: String): LiveData<PagedList<ShowEntity>>

    fun getMovieDetail(showId: String): LiveData<Resource<ShowEntity>>

    fun getTvShowDetail(showId: String): LiveData<Resource<ShowEntity>>

    fun setShowFavorite(show: ShowEntity, state: Boolean)
}