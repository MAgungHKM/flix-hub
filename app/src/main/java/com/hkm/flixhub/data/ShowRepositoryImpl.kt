package com.hkm.flixhub.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hkm.flixhub.data.source.NetworkBoundResource
import com.hkm.flixhub.data.source.local.LocalDataSource
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.data.source.remote.ApiResponse
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.data.source.remote.response.MovieDetailResponse
import com.hkm.flixhub.data.source.remote.response.MovieDiscoveryResponse
import com.hkm.flixhub.data.source.remote.response.TvShowDetailResponse
import com.hkm.flixhub.data.source.remote.response.TvShowDiscoveryResponse
import com.hkm.flixhub.utils.AppExecutors
import com.hkm.flixhub.utils.Formatter
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE
import com.hkm.flixhub.utils.PaginationUtils.PAGE_LIST_PAGE_SIZE
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.vo.Resource

class ShowRepositoryImpl constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors,
) : ShowRepository {
    override fun getAllMovies(page: String): LiveData<Resource<PagedList<ShowEntity>>> {
        return object :
            NetworkBoundResource<PagedList<ShowEntity>, MovieDiscoveryResponse>(appExecutors) {
            override fun loadFromDB(): LiveData<PagedList<ShowEntity>> {
                val config = PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPageSize(PAGE_LIST_PAGE_SIZE)
                    .build()
                return LivePagedListBuilder(localDataSource.getAllMovie(page), config).build()
            }

            override fun shouldFetch(data: PagedList<ShowEntity>?): Boolean =
                data.isNullOrEmpty() || data.size < page.toInt() * ITEM_PER_PAGE

            override fun createCall(): LiveData<ApiResponse<MovieDiscoveryResponse>> =
                remoteDataSource.getAllMovie(page)

            override fun saveCallResult(data: MovieDiscoveryResponse) {
                val movieList = ArrayList<ShowEntity>()
                val results = data.results
                for (result in results) {
                    val posterPath = "https://image.tmdb.org/t/p/w780" + result.posterPath
                    val show = ShowEntity(showId = result.id.toString(),
                        type = ShowType.TYPE_MOVIE,
                        title = result.title,
                        posterPath = posterPath
                    )

                    movieList.add(show)
                }

                localDataSource.insertShows(movieList)
            }
        }.asLiveData()
    }

    override fun getAllTvShows(page: String): LiveData<Resource<PagedList<ShowEntity>>> {
        return object :
            NetworkBoundResource<PagedList<ShowEntity>, TvShowDiscoveryResponse>(appExecutors) {
            override fun loadFromDB(): LiveData<PagedList<ShowEntity>> {
                val config = PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPageSize(PAGE_LIST_PAGE_SIZE)
                    .build()
                return LivePagedListBuilder(localDataSource.getAllTvShow(page), config).build()
            }

            override fun shouldFetch(data: PagedList<ShowEntity>?): Boolean =
                data.isNullOrEmpty() || data.size < page.toInt() * ITEM_PER_PAGE

            override fun createCall(): LiveData<ApiResponse<TvShowDiscoveryResponse>> =
                remoteDataSource.getAllTvShow(page)

            override fun saveCallResult(data: TvShowDiscoveryResponse) {
                val tvShowList = ArrayList<ShowEntity>()
                val results = data.results
                for (result in results) {
                    val posterPath = "https://image.tmdb.org/t/p/w780" + result.posterPath
                    val show = ShowEntity(showId = result.id.toString(),
                        type = ShowType.TYPE_TV_SHOW,
                        title = result.name,
                        posterPath = posterPath
                    )

                    tvShowList.add(show)
                }

                localDataSource.insertShows(tvShowList)
            }
        }.asLiveData()
    }

    override fun getMovieDetail(showId: String): LiveData<Resource<ShowEntity>> {
        return object : NetworkBoundResource<ShowEntity, MovieDetailResponse>(appExecutors) {
            override fun loadFromDB(): LiveData<ShowEntity> =
                localDataSource.getShowDetail(showId)

            override fun shouldFetch(data: ShowEntity?): Boolean =
                data?.synopsis == "null" || data?.genre == "null" || data?.releaseDate == "null" || data?.score == "null"

            override fun createCall(): LiveData<ApiResponse<MovieDetailResponse>> =
                remoteDataSource.getMovieDetail(showId)

            override fun saveCallResult(data: MovieDetailResponse) {
                val score = data.voteAverage.toString().replace(".", "").plus("%")
                val genres = data.genres.joinToString { it.name }
                val posterPath = "https://image.tmdb.org/t/p/w780${data.posterPath}"
                val bannerPath = if (data.backdropPath != "null")
                    "https://image.tmdb.org/t/p/w1280${data.backdropPath}" else posterPath
                val quote =
                    if (data.tagline != "") data.tagline else "null"
                val date = Formatter.dateFormatter(data.releaseDate)

                lateinit var director: String
                val crewList = data.crew
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

                val show = ShowEntity(
                    showId = data.id.toString(),
                    title = data.title,
                    type = ShowType.TYPE_MOVIE,
                    synopsis = data.overview,
                    releaseDate = date,
                    director = director,
                    quote = quote,
                    score = score,
                    genre = genres,
                    bannerPath = bannerPath,
                    posterPath = posterPath,
                )

                localDataSource.updateShow(show)
            }

        }.asLiveData()
    }

    override fun getTvShowDetail(showId: String): LiveData<Resource<ShowEntity>> {
        return object : NetworkBoundResource<ShowEntity, TvShowDetailResponse>(appExecutors) {
            override fun loadFromDB(): LiveData<ShowEntity> =
                localDataSource.getShowDetail(showId)

            override fun shouldFetch(data: ShowEntity?): Boolean =
                data?.synopsis == "null" || data?.genre == "null" || data?.releaseDate == "null" || data?.score == "null"

            override fun createCall(): LiveData<ApiResponse<TvShowDetailResponse>> =
                remoteDataSource.getTvShowDetail(showId)

            override fun saveCallResult(data: TvShowDetailResponse) {
                val score = data.voteAverage.toString().replace(".", "").plus("%")
                val genres = data.genres.joinToString { it.name }
                val posterPath = "https://image.tmdb.org/t/p/w780${data.posterPath}"
                val bannerPath = if (data.backdropPath != "null")
                    "https://image.tmdb.org/t/p/w1280${data.backdropPath}" else posterPath
                val quote =
                    if (data.tagline != "") data.tagline else "null"
                val date = Formatter.dateFormatter(data.firstAirDate)

                val creators = data.createdBy
                val director = if (!creators.isNullOrEmpty()) creators[0].name else "null"

                val show = ShowEntity(
                    showId = data.id.toString(),
                    title = data.name,
                    type = ShowType.TYPE_TV_SHOW,
                    synopsis = data.overview,
                    releaseDate = date,
                    director = director,
                    quote = quote,
                    score = score,
                    genre = genres,
                    bannerPath = bannerPath,
                    posterPath = posterPath,
                )

                localDataSource.updateShow(show)
            }

        }.asLiveData()
    }

    override fun getFavoritedMovies(page: String): LiveData<PagedList<ShowEntity>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_LIST_PAGE_SIZE)
            .build()
        return LivePagedListBuilder(localDataSource.getFavoritedMovies(page), config).build()
    }

    override fun getFavoritedTvShows(page: String): LiveData<PagedList<ShowEntity>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_LIST_PAGE_SIZE)
            .build()
        return LivePagedListBuilder(localDataSource.getFavoritedTvShows(page), config).build()
    }

    override fun setShowFavorite(show: ShowEntity, state: Boolean) =
        appExecutors.diskIO().execute { localDataSource.setShowFavorite(show, state) }
}