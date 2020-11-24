package com.hkm.flixhub.data

data class ShowEntity(
    val showId: String,
    val title: String,
    val synopsis: String,
    val releaseDate: String,
    val director: String,
    val quote: String,
    val score: String,
    val genre: String,
    val imagePath: Int
)