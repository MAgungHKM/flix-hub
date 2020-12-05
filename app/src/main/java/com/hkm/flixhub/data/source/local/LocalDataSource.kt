package com.hkm.flixhub.data.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.sqlite.db.SupportSQLiteQuery
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.data.source.local.room.ShowDao
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE
import com.hkm.flixhub.utils.ShowType

class LocalDataSource constructor(private val mShowDao: ShowDao) {
    fun getAllMovie(page: String): DataSource.Factory<Int, ShowEntity> =
        mShowDao.getShows(ShowType.TYPE_MOVIE, (page.toInt() * ITEM_PER_PAGE))

    fun getAllTvShow(page: String): DataSource.Factory<Int, ShowEntity> =
        mShowDao.getShows(ShowType.TYPE_TV_SHOW, (page.toInt() * ITEM_PER_PAGE))

    fun getFavoritedMovies(query: SupportSQLiteQuery): DataSource.Factory<Int, ShowEntity> =
        mShowDao.getFavoritedShows(query)

    fun getFavoritedTvShows(query: SupportSQLiteQuery): DataSource.Factory<Int, ShowEntity> =
        mShowDao.getFavoritedShows(query)

    fun getShowDetail(showId: String): LiveData<ShowEntity> = mShowDao.getShowByShowId(showId)

    fun insertShows(shows: List<ShowEntity>) = mShowDao.insertMovies(shows)

    fun updateShow(show: ShowEntity) = mShowDao.updateShow(show)

    fun setShowFavorite(show: ShowEntity, newState: Boolean) {
        show.favorited = newState
        mShowDao.updateShow(show)
    }

    fun deleteAllExceptFavorite(showType: String) = mShowDao.deleteAllExceptFavorite(showType)
}