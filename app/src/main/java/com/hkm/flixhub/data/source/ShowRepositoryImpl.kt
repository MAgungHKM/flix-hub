package com.hkm.flixhub.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hkm.flixhub.data.source.local.entity.DetailShowEntity
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.data.source.remote.response.*
import com.hkm.flixhub.utils.Formatter

class ShowRepositoryImpl constructor(private val remoteDataSource: RemoteDataSource) :
    ShowRepository {
    override fun getAllMovies(): LiveData<ArrayList<ShowEntity>> {
        val movieResults = MutableLiveData<ArrayList<ShowEntity>>()
        remoteDataSource.getAllMovie(object : RemoteDataSource.LoadMoviesCallback {
            override fun onAllMoviesReceived(movieDiscoveryResponse: MovieDiscoveryResponse) {
                val movieList = ArrayList<ShowEntity>()
                val results = movieDiscoveryResponse.results
                for (result in results) {
                    val posterPath = "https://image.tmdb.org/t/p/w780" + result.posterPath
                    val show = ShowEntity(result.id.toString(),
                        result.title,
                        posterPath,
                        "null"
                    )

                    movieList.add(show)
                }
                movieResults.postValue(movieList)
            }

            override fun onErrorReceived(errorMessage: String) {
                val movieList = ArrayList<ShowEntity>()
                val show = ShowEntity("null",
                    "null",
                    "null",
                    errorMessage
                )

                movieList.add(show)
                movieResults.postValue(movieList)
            }
        })

        return movieResults
    }

    override fun getAllTvShows(): LiveData<ArrayList<ShowEntity>> {
        val tvShowResults = MutableLiveData<ArrayList<ShowEntity>>()
        remoteDataSource.getAllTvShow(object : RemoteDataSource.LoadTvShowsCallback {
            override fun onAllTvShowsReceived(tvShowDiscoveryResponse: TvShowDiscoveryResponse) {
                val tvShowList = ArrayList<ShowEntity>()
                val results = tvShowDiscoveryResponse.results
                for (result in results) {
                    val posterPath = "https://image.tmdb.org/t/p/w780" + result.posterPath
                    val show = ShowEntity(result.id.toString(),
                        result.name,
                        posterPath,
                        "null"
                    )

                    tvShowList.add(show)
                }
                tvShowResults.postValue(tvShowList)
            }

            override fun onErrorReceived(errorMessage: String) {
                val tvShowList = ArrayList<ShowEntity>()
                val show = ShowEntity("null",
                    "null",
                    "null",
                    errorMessage
                )

                tvShowList.add(show)
                tvShowResults.postValue(tvShowList)
            }
        })

        return tvShowResults
    }

    override fun getMovieDetail(showId: String): LiveData<DetailShowEntity> {
        val movieDetail = MutableLiveData<DetailShowEntity>()
        lateinit var director: String
        remoteDataSource.getMovieDetail(showId, object : RemoteDataSource.LoadMovieDetailCallback {
            override fun onMovieDetailReceived(movieDetailResponse: MovieDetailResponse) {

                val score = movieDetailResponse.voteAverage.toString().replace(".", "").plus("%")
                val genres = movieDetailResponse.genres.joinToString { it.name }
                val bannerPath =
                    "https://image.tmdb.org/t/p/w1280${movieDetailResponse.backdropPath}"
                val posterPath = "https://image.tmdb.org/t/p/w780${movieDetailResponse.posterPath}"
                val quote =
                    if (movieDetailResponse.tagline != "") movieDetailResponse.tagline else "null"
                val date = Formatter.dateFormatter(movieDetailResponse.releaseDate)

                val detail = DetailShowEntity(movieDetailResponse.id.toString(),
                    movieDetailResponse.title,
                    movieDetailResponse.overview,
                    date,
                    director,
                    quote,
                    score,
                    genres,
                    bannerPath,
                    posterPath,
                    "null"
                )

                movieDetail.postValue(detail)
            }

            override fun onMovieCreditsReceived(movieCreditsResponse: MovieCreditsResponse) {
                val crewList = movieCreditsResponse.crew
                var found = false
                if (!crewList.isNullOrEmpty()) {
                    for (crew in crewList) {
                        if (crew.job == "Director") {
                            director = crew.name
                            found = true
                            break
                        }
                    }

                    if (!found)
                        director = "null"
                } else
                    director = "null"
            }

            override fun onErrorReceived(errorMessage: String) {
                val detail = DetailShowEntity("null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    "null",
                    errorMessage
                )

                movieDetail.postValue(detail)
            }
        })

        return movieDetail
    }

    override fun getTvShowDetail(showId: String): LiveData<DetailShowEntity> {
        val tvShowDetail = MutableLiveData<DetailShowEntity>()
        remoteDataSource.getTvShowDetail(showId,
            object : RemoteDataSource.LoadTvShowDetailCallback {
                override fun onTvShowDetailReceived(tvShowDetailResponse: TvShowDetailResponse) {
                    val creators = tvShowDetailResponse.createdBy
                    val director = if (!creators.isNullOrEmpty()) creators[0].name else "null"

                    val score =
                        tvShowDetailResponse.voteAverage.toString().replace(".", "").plus("%")
                    val genres = tvShowDetailResponse.genres.joinToString { it.name }
                    val bannerPath =
                        "https://image.tmdb.org/t/p/w1280${tvShowDetailResponse.backdropPath}"
                    val posterPath =
                        "https://image.tmdb.org/t/p/w780${tvShowDetailResponse.posterPath}"
                    val quote =
                        if (tvShowDetailResponse.tagline != "") tvShowDetailResponse.tagline else "null"
                    val date = Formatter.dateFormatter(tvShowDetailResponse.firstAirDate)

                    val detail = DetailShowEntity(tvShowDetailResponse.id.toString(),
                        tvShowDetailResponse.name,
                        tvShowDetailResponse.overview,
                        date,
                        director,
                        quote,
                        score,
                        genres,
                        bannerPath,
                        posterPath,
                        "null"
                    )

                    tvShowDetail.postValue(detail)
                }

                override fun onErrorReceived(errorMessage: String) {
                    val detail = DetailShowEntity("null",
                        "null",
                        "null",
                        "null",
                        "null",
                        "null",
                        "null",
                        "null",
                        "null",
                        "null",
                        errorMessage
                    )

                    tvShowDetail.postValue(detail)
                }
            })

        return tvShowDetail
    }
}