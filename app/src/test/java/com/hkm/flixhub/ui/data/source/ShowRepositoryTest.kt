package com.hkm.flixhub.ui.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.agoda.kakao.common.utilities.getResourceString
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.utils.DataDummy
import com.hkm.flixhub.utils.Formatter
import com.hkm.flixhub.utils.LiveDataTestUtil
import io.mockk.every
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
        app.loadModules(module {
            single { RemoteDataSource(get()) }
        }) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mShowRepository = FakeShowRepositoryImpl(mRemoteDataSource)

            every { mRemoteDataSource.getAllMovie(any()) } answers { call ->
                (call.invocation.args[0] as RemoteDataSource.LoadMoviesCallback)
                    .onAllMoviesReceived(movieResponse)
            }

            val movieEntities = LiveDataTestUtil.getValue(mShowRepository.getAllMovies())
            verify { mRemoteDataSource.getAllMovie(any()) }
            assertNotNull(movieEntities)
            assertEquals(movieResponse.results.size.toLong(), movieEntities.size.toLong())
        }
    }

    @Test
    fun fetchAllTvShows() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
        }) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mShowRepository = FakeShowRepositoryImpl(mRemoteDataSource)

            every { mRemoteDataSource.getAllTvShow(any()) } answers { call ->
                (call.invocation.args[0] as RemoteDataSource.LoadTvShowsCallback)
                    .onAllTvShowsReceived(tvShowResponse)
            }

            val tvShowEntities = LiveDataTestUtil.getValue(mShowRepository.getAllTvShows())
            verify { mRemoteDataSource.getAllTvShow(any()) }
            assertNotNull(tvShowEntities)
            assertEquals(tvShowResponse.results.size.toLong(), tvShowEntities.size.toLong())
        }
    }

    @Test
    fun fetchMovieDetail() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
        }) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mShowRepository = FakeShowRepositoryImpl(mRemoteDataSource)

            every { mRemoteDataSource.getMovieDetail(movieId, any()) } answers { call ->
                with(call.invocation.args[1] as RemoteDataSource.LoadMovieDetailCallback) {
                    onMovieCreditsReceived(movieCreditsResponse)
                    onMovieDetailReceived(movieDetailResponse)
                }
            }

            val movieDetailEntity =
                LiveDataTestUtil.getValue(mShowRepository.getMovieDetail(movieId))
            verify { mRemoteDataSource.getMovieDetail(movieId, any()) }
            assertNotNull(movieDetailEntity)

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
            val releaseDate = Formatter.dateFormatter(movieDetailResponse.releaseDate)
            val errorMessage = "null"

            assertEquals(movieDetailResponse.id.toString(), movieDetailEntity.showId)
            assertEquals(movieDetailResponse.title, movieDetailEntity.title)
            assertEquals(movieDetailResponse.overview, movieDetailEntity.synopsis)
            assertEquals(quote, movieDetailEntity.quote)
            assertEquals(director, movieDetailEntity.director)
            assertEquals(score, movieDetailEntity.score)
            assertEquals(posterPath, movieDetailEntity.posterPath)
            assertEquals(bannerPath, movieDetailEntity.bannerPath)
            assertEquals(genre, movieDetailEntity.genre)
            assertEquals(releaseDate, movieDetailEntity.releaseDate)
            assertEquals(errorMessage, movieDetailEntity.errorMessage)
        }
    }

    @Test
    fun fetchTvShowDetail() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
        }) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mShowRepository = FakeShowRepositoryImpl(mRemoteDataSource)

            every { mRemoteDataSource.getTvShowDetail(tvShowId, any()) } answers { call ->
                (call.invocation.args[1] as RemoteDataSource.LoadTvShowDetailCallback)
                    .onTvShowDetailReceived(tvShowDetailResponse)
            }

            val tvShowDetailEntity =
                LiveDataTestUtil.getValue(mShowRepository.getTvShowDetail(tvShowId))
            verify { mRemoteDataSource.getTvShowDetail(tvShowId, any()) }
            assertNotNull(tvShowDetailEntity)

            val creators = tvShowDetailResponse.createdBy
            val director = if (!creators.isNullOrEmpty()) creators[0].name else "null"
            val score = tvShowDetailResponse.voteAverage.toString().replace(".", "").plus("%")
            val posterPath = "https://image.tmdb.org/t/p/w780${tvShowDetailResponse.posterPath}"
            val bannerPath = "https://image.tmdb.org/t/p/w1280${tvShowDetailResponse.backdropPath}"
            val genre = tvShowDetailResponse.genres.joinToString { it.name }
            val quote =
                if (tvShowDetailResponse.tagline != "") tvShowDetailResponse.tagline else "null"
            val releaseDate = Formatter.dateFormatter(tvShowDetailResponse.firstAirDate)
            val errorMessage = "null"

            assertEquals(tvShowDetailResponse.id.toString(), tvShowDetailEntity.showId)
            assertEquals(tvShowDetailResponse.name, tvShowDetailEntity.title)
            assertEquals(tvShowDetailResponse.overview, tvShowDetailEntity.synopsis)
            assertEquals(quote, tvShowDetailEntity.quote)
            assertEquals(director, tvShowDetailEntity.director)
            assertEquals(score, tvShowDetailEntity.score)
            assertEquals(posterPath, tvShowDetailEntity.posterPath)
            assertEquals(bannerPath, tvShowDetailEntity.bannerPath)
            assertEquals(genre, tvShowDetailEntity.genre)
            assertEquals(releaseDate, tvShowDetailEntity.releaseDate)
            assertEquals(errorMessage, tvShowDetailEntity.errorMessage)
        }
    }

    @Test
    fun fetchDetailWithWrongId() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
        }) {
            mRemoteDataSource = spyk(get<RemoteDataSource>())
            mShowRepository = FakeShowRepositoryImpl(mRemoteDataSource)

            val wrongId = "-1"
            val errorCode = 401
            val statusCode = 34
            val statusMessage = "The resource you requested could not be found."
            val errorMessage = getResourceString(R.string.error_message)
                .replace("%1\$d", errorCode.toString())
                .replace("%2\$d", statusCode.toString())
                .replace("%3\$s", statusMessage)

            every { mRemoteDataSource.getTvShowDetail(wrongId, any()) } answers { call ->
                (call.invocation.args[1] as RemoteDataSource.LoadTvShowDetailCallback)
                    .onErrorReceived(errorMessage)
            }

            val tvShowDetailEntity =
                LiveDataTestUtil.getValue(mShowRepository.getTvShowDetail(wrongId))
            verify { mRemoteDataSource.getTvShowDetail(wrongId, any()) }
            assertNotNull(tvShowDetailEntity)

            val emptyDetailShowEntity = DataDummy.generateEmptyShowDetail(errorMessage)

            assertEquals(emptyDetailShowEntity.showId, tvShowDetailEntity.showId)
            assertEquals(emptyDetailShowEntity.title, tvShowDetailEntity.title)
            assertEquals(emptyDetailShowEntity.synopsis, tvShowDetailEntity.synopsis)
            assertEquals(emptyDetailShowEntity.quote, tvShowDetailEntity.quote)
            assertEquals(emptyDetailShowEntity.director, tvShowDetailEntity.director)
            assertEquals(emptyDetailShowEntity.score, tvShowDetailEntity.score)
            assertEquals(emptyDetailShowEntity.posterPath, tvShowDetailEntity.posterPath)
            assertEquals(emptyDetailShowEntity.bannerPath, tvShowDetailEntity.bannerPath)
            assertEquals(emptyDetailShowEntity.genre, tvShowDetailEntity.genre)
            assertEquals(emptyDetailShowEntity.releaseDate, tvShowDetailEntity.releaseDate)
            assertEquals(emptyDetailShowEntity.errorMessage, tvShowDetailEntity.errorMessage)
        }
    }
}