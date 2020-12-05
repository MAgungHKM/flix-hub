package com.hkm.flixhub.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.hkm.flixhub.data.source.local.entity.ShowEntity

@Dao
interface ShowDao {

    @Query("SELECT * FROM show_entities WHERE type = :showType LIMIT :page")
    fun getShows(showType: String, page: Int): DataSource.Factory<Int, ShowEntity>

    @Query("SELECT * FROM show_entities WHERE type = :showType AND favorited = 1 LIMIT :page")
    fun getFavoritedShows(showType: String, page: Int): DataSource.Factory<Int, ShowEntity>

    @Transaction
    @Query("SELECT * FROM show_entities WHERE showId = :showId")
    fun getShowByShowId(showId: String): LiveData<ShowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(shows: List<ShowEntity>)

    @Update
    fun updateShow(show: ShowEntity)

    @Query("DELETE FROM show_entities WHERE type = :showType AND favorited != 1")
    fun deleteAllExceptFavorite(showType: String)
}