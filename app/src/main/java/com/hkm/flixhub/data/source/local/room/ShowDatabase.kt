package com.hkm.flixhub.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hkm.flixhub.data.source.local.entity.ShowEntity

@Database(entities = [ShowEntity::class],
    version = 1,
    exportSchema = false)
abstract class ShowDatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
}