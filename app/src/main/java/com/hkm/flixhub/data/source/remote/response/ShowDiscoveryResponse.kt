package com.hkm.flixhub.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MovieDiscoveryResponse(

    @field:SerializedName("results")
    val results: List<MovieResultsItem>,

    @field:SerializedName("status_message")
    val statusMessage: String = "",

    @field:SerializedName("status_code")
    val statusCode: Int = 200,

    @field:SerializedName("success")
    val success: Boolean = true,
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

    @field:SerializedName("status_message")
    val statusMessage: String = "",

    @field:SerializedName("status_code")
    val statusCode: Int = 200,

    @field:SerializedName("success")
    val success: Boolean = true,
)

data class TvShowResultsItem(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("poster_path")
    val posterPath: String,
)
