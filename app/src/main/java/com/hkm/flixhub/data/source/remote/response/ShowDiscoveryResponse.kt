package com.hkm.flixhub.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MovieDiscoveryResponse(

    @field:SerializedName("results")
    val results: List<MovieResultsItem>,
)

data class MovieResultsItem(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("poster_path")
    val posterPath: String,
)

data class TvShowDiscoveryResponse(

    @field:SerializedName("results")
    val results: List<TvShowResultsItem>,
)

data class TvShowResultsItem(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("poster_path")
    val posterPath: String,
)
