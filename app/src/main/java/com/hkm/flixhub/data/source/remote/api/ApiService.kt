package com.hkm.flixhub.data.source.remote.api

import com.hkm.flixhub.data.source.remote.response.MovieDetailResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("movie/{id}")
    fun getMovieDetail(
            @Path("id") id: String,
            @Query("api_key") apiKey: String,
            @Query("language") language: String = "en-US"
    ): Call<MovieDetailResponse>
}