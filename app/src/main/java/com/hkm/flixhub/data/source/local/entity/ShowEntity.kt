package com.hkm.flixhub.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show_entities")
data class ShowEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "showId")
    val showId: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "synopsis")
    val synopsis: String = "null",

    @ColumnInfo(name = "releaseDate")
    val releaseDate: String = "null",

    @ColumnInfo(name = "director")
    val director: String = "null",

    @ColumnInfo(name = "quote")
    val quote: String = "null",

    @ColumnInfo(name = "score")
    val score: String = "null",

    @ColumnInfo(name = "genre")
    val genre: String = "null",

    @ColumnInfo(name = "posterPath")
    val posterPath: String,

    @ColumnInfo(name = "bannerPath")
    var bannerPath: String = "null",

    @ColumnInfo(name = "favorited")
    var favorited: Boolean = false,

    @ColumnInfo(name = "errorMessage")
    val errorMessage: String = "null",
)