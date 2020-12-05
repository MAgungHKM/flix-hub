package com.hkm.flixhub.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.SortUtils.DEFAULT
import com.hkm.flixhub.utils.SortUtils.POPULARITY
import com.hkm.flixhub.vo.Resource

interface FakeShowRepository {
    fun getAllMovies(
        sort: String = POPULARITY,
        page: String = FIRST_PAGE,
    ): LiveData<Resource<PagedList<ShowEntity>>>

    fun getAllTvShows(
        sort: String = POPULARITY,
        page: String = FIRST_PAGE,
    ): LiveData<Resource<PagedList<ShowEntity>>>

    fun getFavoritedMovies(
        sort: String = DEFAULT,
        page: String = FIRST_PAGE,
    ): LiveData<PagedList<ShowEntity>>

    fun getFavoritedTvShows(
        sort: String = DEFAULT,
        page: String = FIRST_PAGE,
    ): LiveData<PagedList<ShowEntity>>

    fun getMovieDetail(showId: String): LiveData<Resource<ShowEntity>>

    fun getTvShowDetail(showId: String): LiveData<Resource<ShowEntity>>

    fun setShowFavorite(show: ShowEntity, state: Boolean)
}