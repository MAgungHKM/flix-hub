package com.hkm.flixhub.ui.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.test.core.app.ApplicationProvider
import com.agoda.kakao.common.utilities.getResourceString
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.local.LocalDataSource
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.di.databaseModule
import com.hkm.flixhub.utils.*
import com.hkm.flixhub.vo.Resource
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 21, maxSdk = 28, application = FlixHubTest::class)
class ShowRepositoryTest : KoinTest {
    private lateinit var mRemoteDataSource: RemoteDataSource
    private lateinit var mLocalDataSource: LocalDataSource
    private lateinit var mAppExecutors: AppExecutors
    private lateinit var mShowRepository: FakeShowRepositoryImpl
    private val movieResponse = DataDummy.generateRemoteDummyMovies()
    private val tvShowResponse = DataDummy.generateRemoteDummyTvShows()
    private val movieId = movieResponse.results[0].id.toString()
    private val tvShowId = tvShowResponse.results[0].id.toString()

    private val movieDetailResponse = DataDummy.generateRemoteDummyMoviesDetail()
    private val tvShowDetailResponse = DataDummy.generateRemoteDummyTvShowsDetail()
    private val movieCreditsResponse = DataDummy.generateRemoteDummyMovieCredits()

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun fetchAllMovies() {
        app.loadModules(listOf(
            module {
                single { RemoteDataSource(androidContext()) }
                single { LocalDataSource(get()) }
                single { AppExecutors() }
            },
            databaseModule
        )) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mLocalDataSource = spyk(get<LocalDataSource>())
            mAppExecutors = spyk(get<AppExecutors>())
            mShowRepository =
                FakeShowRepositoryImpl(mRemoteDataSource, mLocalDataSource, mAppExecutors)

            val dataSourceFactory = mockk<DataSource.Factory<Int, ShowEntity>>(relaxed = true)
            every { mLocalDataSource.getAllMovie(any()) } returns dataSourceFactory
            mShowRepository.getAllMovies()

            val movieEntities =
                Resource.success(PagedListUtil.mockKPagedList(DataDummy.generateDummyMovies()))
            verify { mLocalDataSource.getAllMovie(any()) }
            assertNotNull(movieEntities.data)
            assertEquals(movieResponse.results.size.toLong(), movieEntities.data?.size?.toLong())
        }
    }

    @Test
    fun fetchAllTvShows() {
        app.loadModules(listOf(
            module {
                single { RemoteDataSource(androidContext()) }
                single { LocalDataSource(get()) }
                single { AppExecutors() }
            },
            databaseModule
        )) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mLocalDataSource = spyk(get<LocalDataSource>())
            mAppExecutors = spyk(get<AppExecutors>())
            mShowRepository =
                FakeShowRepositoryImpl(mRemoteDataSource, mLocalDataSource, mAppExecutors)

            val dataSourceFactory = mockk<DataSource.Factory<Int, ShowEntity>>(relaxed = true)
            every { mLocalDataSource.getAllTvShow(any()) } returns dataSourceFactory
            mShowRepository.getAllTvShows()

            val tvShowEntities =
                Resource.success(PagedListUtil.mockKPagedList(DataDummy.generateDummyTvShows()))
            verify { mLocalDataSource.getAllTvShow(any()) }
            assertNotNull(tvShowEntities)
            assertEquals(tvShowResponse.results.size.toLong(), tvShowEntities.data?.size?.toLong())
        }
    }

    @Test
    fun fetchMovieDetail() {
        app.loadModules(listOf(
            module {
                single { RemoteDataSource(androidContext()) }
                single { LocalDataSource(get()) }
                single { AppExecutors() }
            },
            databaseModule
        )) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mLocalDataSource = spyk(get<LocalDataSource>())
            mAppExecutors = spyk(get<AppExecutors>())
            mShowRepository =
                FakeShowRepositoryImpl(mRemoteDataSource, mLocalDataSource, mAppExecutors)

            val dummyDetail = MutableLiveData<ShowEntity>()
            dummyDetail.value = DataDummy.generateDummyMoviesDetail()[0]
            every { mLocalDataSource.getShowDetail(movieId) } returns dummyDetail

            val movieDetail = LiveDataTestUtil.getValue(mShowRepository.getMovieDetail(movieId))
            verify { mLocalDataSource.getShowDetail(movieId) }
            assertNotNull(movieDetail)
            assertNotNull(movieDetail.data)

            var director = "null"
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

            val score = movieDetailResponse.voteAverage.toString().replace(".", "").plus("%")
            val posterPath = "https://image.tmdb.org/t/p/w780${movieDetailResponse.posterPath}"
            val bannerPath = "https://image.tmdb.org/t/p/w1280${movieDetailResponse.backdropPath}"
            val genre = movieDetailResponse.genres.joinToString { it.name }
            val quote =
                if (movieDetailResponse.tagline != "") movieDetailResponse.tagline else "null"
            val releaseDate = Formatter.dateFormatter(movieDetailResponse.releaseDate.toString())
            val errorMessage = "null"

            assertEquals(movieDetailResponse.id.toString(), movieDetail.data?.showId)
            assertEquals(ShowType.TYPE_MOVIE, movieDetail.data?.type)
            assertEquals(movieDetailResponse.title, movieDetail.data?.title)
            assertEquals(movieDetailResponse.overview, movieDetail.data?.synopsis)
            assertEquals(quote, movieDetail.data?.quote)
            assertEquals(director, movieDetail.data?.director)
            assertEquals(score, movieDetail.data?.score)
            assertEquals(posterPath, movieDetail.data?.posterPath)
            assertEquals(bannerPath, movieDetail.data?.bannerPath)
            assertEquals(genre, movieDetail.data?.genre)
            assertEquals(releaseDate, movieDetail.data?.releaseDate)
            assertEquals(errorMessage, movieDetail.data?.errorMessage)
        }
    }

    @Test
    fun fetchTvShowDetail() {
        app.loadModules(listOf(
            module {
                single { RemoteDataSource(androidContext()) }
                single { LocalDataSource(get()) }
                single { AppExecutors() }
            },
            databaseModule
        )) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mLocalDataSource = spyk(get<LocalDataSource>())
            mAppExecutors = spyk(get<AppExecutors>())
            mShowRepository =
                FakeShowRepositoryImpl(mRemoteDataSource, mLocalDataSource, mAppExecutors)

            val dummyDetail = MutableLiveData<ShowEntity>()
            dummyDetail.value = DataDummy.generateDummyTvShowsDetail()[0]
            every { mLocalDataSource.getShowDetail(tvShowId) } returns dummyDetail

            val tvShowDetail = LiveDataTestUtil.getValue(mShowRepository.getTvShowDetail(tvShowId))
            verify { mLocalDataSource.getShowDetail(tvShowId) }
            assertNotNull(tvShowDetail)
            assertNotNull(tvShowDetail.data)

            val creators = tvShowDetailResponse.createdBy
            val director = if (!creators.isNullOrEmpty()) creators[0].name else "null"
            val score = tvShowDetailResponse.voteAverage.toString().replace(".", "").plus("%")
            val posterPath = "https://image.tmdb.org/t/p/w780${tvShowDetailResponse.posterPath}"
            val bannerPath = "https://image.tmdb.org/t/p/w1280${tvShowDetailResponse.backdropPath}"
            val genre = tvShowDetailResponse.genres.joinToString { it.name }
            val quote =
                if (tvShowDetailResponse.tagline != "") tvShowDetailResponse.tagline else "null"
            val releaseDate = Formatter.dateFormatter(tvShowDetailResponse.firstAirDate.toString())
            val errorMessage = "null"

            assertEquals(tvShowDetailResponse.id.toString(), tvShowDetail.data?.showId)
            assertEquals(ShowType.TYPE_TV_SHOW, tvShowDetail.data?.type)
            assertEquals(tvShowDetailResponse.name, tvShowDetail.data?.title)
            assertEquals(tvShowDetailResponse.overview, tvShowDetail.data?.synopsis)
            assertEquals(quote, tvShowDetail.data?.quote)
            assertEquals(director, tvShowDetail.data?.director)
            assertEquals(score, tvShowDetail.data?.score)
            assertEquals(posterPath, tvShowDetail.data?.posterPath)
            assertEquals(bannerPath, tvShowDetail.data?.bannerPath)
            assertEquals(genre, tvShowDetail.data?.genre)
            assertEquals(releaseDate, tvShowDetail.data?.releaseDate)
            assertEquals(errorMessage, tvShowDetail.data?.errorMessage)
        }
    }

    @Test
    fun fetchDetailWithWrongId() {
        app.loadModules(listOf(
            module {
                single { RemoteDataSource(androidContext()) }
                single { LocalDataSource(get()) }
                single { AppExecutors() }
            },
            databaseModule
        )) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mLocalDataSource = spyk(get<LocalDataSource>())
            mAppExecutors = spyk(get<AppExecutors>())
            mShowRepository =
                FakeShowRepositoryImpl(mRemoteDataSource, mLocalDataSource, mAppExecutors)

            val wrongId = "-1"
            val errorCode = 401
            val statusCode = 34
            val statusMessage = "The resource you requested could not be found."
            val errorMessage = getResourceString(R.string.error_message)
                .replace("%1\$d", errorCode.toString())
                .replace("%2\$d", statusCode.toString())
                .replace("%3\$s", statusMessage)

            val errorDummy = MutableLiveData<ShowEntity>()
            errorDummy.value = DataDummy.generateEmptyShowDetail(errorMessage)
            every { mLocalDataSource.getShowDetail(wrongId) } returns errorDummy

            val tvShowDetail = LiveDataTestUtil.getValue(mShowRepository.getTvShowDetail(wrongId))
            verify { mLocalDataSource.getShowDetail(wrongId) }
            assertNotNull(tvShowDetail)
            assertNotNull(tvShowDetail.data)

            val emptyDetailShowEntity = DataDummy.generateEmptyShowDetail(errorMessage)

            assertEquals(emptyDetailShowEntity.showId, tvShowDetail.data?.showId)
            assertEquals(emptyDetailShowEntity.type, tvShowDetail.data?.type)
            assertEquals(emptyDetailShowEntity.title, tvShowDetail.data?.title)
            assertEquals(emptyDetailShowEntity.synopsis, tvShowDetail.data?.synopsis)
            assertEquals(emptyDetailShowEntity.quote, tvShowDetail.data?.quote)
            assertEquals(emptyDetailShowEntity.director, tvShowDetail.data?.director)
            assertEquals(emptyDetailShowEntity.score, tvShowDetail.data?.score)
            assertEquals(emptyDetailShowEntity.posterPath, tvShowDetail.data?.posterPath)
            assertEquals(emptyDetailShowEntity.bannerPath, tvShowDetail.data?.bannerPath)
            assertEquals(emptyDetailShowEntity.genre, tvShowDetail.data?.genre)
            assertEquals(emptyDetailShowEntity.releaseDate, tvShowDetail.data?.releaseDate)
            assertEquals(emptyDetailShowEntity.errorMessage, tvShowDetail.data?.errorMessage)
        }
    }
}