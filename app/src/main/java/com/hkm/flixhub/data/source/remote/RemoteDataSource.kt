package com.hkm.flixhub.data.source.remote

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hkm.flixhub.R
import com.hkm.flixhub.api.ApiConfig
import com.hkm.flixhub.data.source.remote.response.*
import com.hkm.flixhub.utils.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource constructor(private val context: Context) {

    companion object {
        private val TAG = RemoteDataSource::class.java.simpleName
    }

    fun getAllMovie(callback: LoadMoviesCallback) {
        EspressoIdlingResource.increment()
        lateinit var movieDiscoveryResponse: MovieDiscoveryResponse
        val client = ApiConfig.getApiService().getMovieDiscovery()
        client.enqueue(object : Callback<MovieDiscoveryResponse> {
            override fun onResponse(
                call: Call<MovieDiscoveryResponse>,
                response: Response<MovieDiscoveryResponse>,
            ) {
                if (response.isSuccessful) {
                    movieDiscoveryResponse = response.body() as MovieDiscoveryResponse
                    callback.onAllMoviesReceived(movieDiscoveryResponse)
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    callback.onErrorReceived(msg)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<MovieDiscoveryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
    }

    fun getAllTvShow(callback: LoadTvShowsCallback) {
        EspressoIdlingResource.increment()
        lateinit var tvShowDiscoveryResponse: TvShowDiscoveryResponse
        val client = ApiConfig.getApiService().getTvShowDiscovery()
        client.enqueue(object : Callback<TvShowDiscoveryResponse> {
            override fun onResponse(
                call: Call<TvShowDiscoveryResponse>,
                response: Response<TvShowDiscoveryResponse>,
            ) {
                if (response.isSuccessful) {
                    tvShowDiscoveryResponse = response.body() as TvShowDiscoveryResponse
                    callback.onAllTvShowsReceived(tvShowDiscoveryResponse)
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    callback.onErrorReceived(msg)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<TvShowDiscoveryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
    }

    fun getMovieDetail(id: String, callback: LoadMovieDetailCallback) {
        EspressoIdlingResource.increment()
        lateinit var movieDetailResponse: MovieDetailResponse
        lateinit var movieCreditsResponse: MovieCreditsResponse
        val client = ApiConfig.getApiService().getMovieDetail(id)
        client.enqueue(object : Callback<MovieDetailResponse> {
            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>,
            ) {
                if (response.isSuccessful) {
                    EspressoIdlingResource.increment()
                    movieDetailResponse = response.body() as MovieDetailResponse
                    val client2 = ApiConfig.getApiService().getMovieCredits(id)
                    client2.enqueue(object : Callback<MovieCreditsResponse> {
                        override fun onResponse(
                            call: Call<MovieCreditsResponse>,
                            response: Response<MovieCreditsResponse>,
                        ) {
                            if (response.isSuccessful) {
                                movieCreditsResponse = response.body() as MovieCreditsResponse
                                callback.onMovieCreditsReceived(movieCreditsResponse)
                                callback.onMovieDetailReceived(movieDetailResponse)
                            } else {
                                val errorResponse =
                                    Gson().fromJson(response.errorBody()?.charStream(),
                                        ErrorResponse::class.java)
                                val msg = context
                                    .getString(R.string.error_message,
                                        response.code(),
                                        errorResponse.statusCode,
                                        errorResponse.statusMessage)
                                Log.e(TAG, msg)
                                callback.onErrorReceived(msg)
                            }
                            EspressoIdlingResource.decrement()
                        }

                        override fun onFailure(call: Call<MovieCreditsResponse>, t: Throwable) {
                            Log.e(TAG, "onFailure: ${t.message.toString()}")
                            EspressoIdlingResource.decrement()
                        }
                    })
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    callback.onErrorReceived(msg)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })
    }

    fun getTvShowDetail(id: String, callback: LoadTvShowDetailCallback) {
        EspressoIdlingResource.increment()
        lateinit var tvShowDetailResponse: TvShowDetailResponse
        val client = ApiConfig.getApiService().getTvShowDetail(id)
        client.enqueue(object : Callback<TvShowDetailResponse> {
            override fun onResponse(
                call: Call<TvShowDetailResponse>,
                response: Response<TvShowDetailResponse>,
            ) {
                if (response.isSuccessful) {
                    tvShowDetailResponse = response.body() as TvShowDetailResponse
                    callback.onTvShowDetailReceived(tvShowDetailResponse)
                } else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(),
                        ErrorResponse::class.java)
                    val msg = context.getString(R.string.error_message,
                        response.code(),
                        errorResponse.statusCode,
                        errorResponse.statusMessage)
                    Log.e(TAG, msg)
                    callback.onErrorReceived(msg)
                }
                EspressoIdlingResource.decrement()
            }

            override fun onFailure(call: Call<TvShowDetailResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                EspressoIdlingResource.decrement()
            }
        })

    }

    interface LoadMoviesCallback {
        fun onAllMoviesReceived(movieDiscoveryResponse: MovieDiscoveryResponse)
        fun onErrorReceived(errorMessage: String)
    }

    interface LoadTvShowsCallback {
        fun onAllTvShowsReceived(tvShowDiscoveryResponse: TvShowDiscoveryResponse)
        fun onErrorReceived(errorMessage: String)
    }

    interface LoadMovieDetailCallback {
        fun onMovieDetailReceived(movieDetailResponse: MovieDetailResponse)
        fun onMovieCreditsReceived(movieCreditsResponse: MovieCreditsResponse)
        fun onErrorReceived(errorMessage: String)
    }

    interface LoadTvShowDetailCallback {
        fun onTvShowDetailReceived(tvShowDetailResponse: TvShowDetailResponse)
        fun onErrorReceived(errorMessage: String)
    }
}