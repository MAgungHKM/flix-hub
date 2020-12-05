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
import com.hkm.flixhub.utils.SortUtils
import com.hkm.flixhub.vo.Resource

class FakeShowRepositoryImpl constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors,
) : FakeShowRepository {
    private var lastMovieSort: String? = null
    private var lastTvShowSort: String? = null

    override fun getAllMovies(
        sort: String,
        page: String,
    ): LiveData<Resource<PagedList<ShowEntity>>> {
        val networkBoundResource = object :
            NetworkBoundResource<PagedList<ShowEntity>, MovieDiscoveryResponse>(appExecutors,
                lastMovieSort) {
            override fun loadFromDB(): LiveData<PagedList<ShowEntity>> {
                val config = PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPageSize(PAGE_LIST_PAGE_SIZE)
                    .build()
                return LivePagedListBuilder(localDataSource.getAllMovie(page), config).build()
            }

            override fun shouldFetch(data: PagedList<ShowEntity>?, lastSort: String?): Boolean =
                data.isNullOrEmpty() || data.size < page.toInt() * ITEM_PER_PAGE || lastSort != sort

            override fun createCall(): LiveData<ApiResponse<MovieDiscoveryResponse>> =
                remoteDataSource.getAllMovie(sort, page)

            override fun saveCallResult(data: MovieDiscoveryResponse, lastSort: String?) {
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

                if (lastSort != null && lastSort != sort)
                    localDataSource.deleteAllExceptFavorite(ShowType.TYPE_MOVIE)

                localDataSource.insertShows(movieList)
            }
        }.asLiveData()

        lastMovieSort = sort

        return networkBoundResource
    }

    override fun getAllTvShows(
        sort: String,
        page: String,
    ): LiveData<Resource<PagedList<ShowEntity>>> {
        val networkBoundResource = object :
            NetworkBoundResource<PagedList<ShowEntity>, TvShowDiscoveryResponse>(appExecutors,
                lastTvShowSort) {
            override fun loadFromDB(): LiveData<PagedList<ShowEntity>> {
                val config = PagedList.Config.Builder()
                    .setEnablePlaceholders(false)
                    .setPageSize(PAGE_LIST_PAGE_SIZE)
                    .build()
                return LivePagedListBuilder(localDataSource.getAllTvShow(page), config).build()
            }

            override fun shouldFetch(data: PagedList<ShowEntity>?, lastSort: String?): Boolean =
                data.isNullOrEmpty() || data.size < page.toInt() * ITEM_PER_PAGE || lastSort != sort

            override fun createCall(): LiveData<ApiResponse<TvShowDiscoveryResponse>> =
                remoteDataSource.getAllTvShow(sort, page)

            override fun saveCallResult(data: TvShowDiscoveryResponse, lastSort: String?) {
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

                if (lastSort != null && lastSort != sort)
                    localDataSource.deleteAllExceptFavorite(ShowType.TYPE_TV_SHOW)

                localDataSource.insertShows(tvShowList)
            }
        }.asLiveData()

        lastTvShowSort = sort

        return networkBoundResource
    }

    override fun getMovieDetail(showId: String): LiveData<Resource<ShowEntity>> {
        return object : NetworkBoundResource<ShowEntity, MovieDetailResponse>(appExecutors) {
            override fun loadFromDB(): LiveData<ShowEntity> =
                localDataSource.getShowDetail(showId)

            override fun shouldFetch(data: ShowEntity?, lastSort: String?): Boolean =
                data?.synopsis == "null" || data?.genre == "null" || data?.releaseDate == "null" || data?.score == "null"

            override fun createCall(): LiveData<ApiResponse<MovieDetailResponse>> =
                remoteDataSource.getMovieDetail(showId)

            override fun saveCallResult(data: MovieDetailResponse, lastSort: String?) {
                val score = data.voteAverage.toString().replace(".", "").plus("%")
                val genres = data.genres.joinToString { it.name }
                val posterPath = "https://image.tmdb.org/t/p/w780${data.posterPath}"
                val bannerPath = if (data.backdropPath != null)
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

            override fun shouldFetch(data: ShowEntity?, lastSort: String?): Boolean =
                data?.synopsis == "null" || data?.genre == "null" || data?.releaseDate == "null" || data?.score == "null"

            override fun createCall(): LiveData<ApiResponse<TvShowDetailResponse>> =
                remoteDataSource.getTvShowDetail(showId)

            override fun saveCallResult(data: TvShowDetailResponse, lastSort: String?) {
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

    override fun getFavoritedMovies(sort: String, page: String): LiveData<PagedList<ShowEntity>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_LIST_PAGE_SIZE)
            .build()

        val pageSize = page.toInt() * ITEM_PER_PAGE
        val query = SortUtils.getSortedQuery(sort, ShowType.TYPE_MOVIE, pageSize)
        return LivePagedListBuilder(localDataSource.getFavoritedMovies(query), config).build()
    }

    override fun getFavoritedTvShows(sort: String, page: String): LiveData<PagedList<ShowEntity>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_LIST_PAGE_SIZE)
            .build()

        val pageSize = page.toInt() * ITEM_PER_PAGE
        val query = SortUtils.getSortedQuery(sort, ShowType.TYPE_TV_SHOW, pageSize)
        return LivePagedListBuilder(localDataSource.getFavoritedTvShows(query), config).build()
    }

    override fun setShowFavorite(show: ShowEntity, state: Boolean) =
        appExecutors.diskIO().execute { localDataSource.setShowFavorite(show, state) }
}