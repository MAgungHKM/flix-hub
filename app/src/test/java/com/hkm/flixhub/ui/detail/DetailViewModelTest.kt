package com.hkm.flixhub.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.agoda.kakao.common.utilities.getResourceString
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.ShowRepositoryImpl
import com.hkm.flixhub.data.source.local.entity.DetailShowEntity
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.utils.DataDummy
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 21, maxSdk = 28, application = FlixHubTest::class)
class DetailViewModelTest : KoinTest {
    private lateinit var mDetailViewModel: DetailViewModel
    private lateinit var mShowRepository: ShowRepository
    private val dataMovie = DataDummy.generateDummyMoviesDetail()[0]
    private val dataTvShow = DataDummy.generateDummyTvShowsDetail()[0]
    private val movieId = dataMovie.showId
    private val tvShowId = dataTvShow.showId

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()
    private val observer: Observer<DetailShowEntity> = mockk(relaxed = true)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun fetchMovies() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
            single<ShowRepository> { ShowRepositoryImpl(get()) }
        }) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = DetailViewModel(mShowRepository)

            val movie = MutableLiveData<DetailShowEntity>()
            movie.value = dataMovie

            mDetailViewModel.setSelectedShow(movieId)
            mDetailViewModel.setSelectedShowType(DetailFragment.TYPE_MOVIE)

            every { mShowRepository.getMovieDetail(movieId) } returns movie
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value as DetailShowEntity
            verify { mShowRepository.getMovieDetail(movieId) }

            assertNotNull(mDetailShowEntity)
            assertEquals(dataMovie.showId, mDetailShowEntity.showId)
            assertEquals(dataMovie.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(dataMovie.synopsis, mDetailShowEntity.synopsis)
            assertEquals(dataMovie.posterPath, mDetailShowEntity.posterPath)
            assertEquals(dataMovie.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(dataMovie.title, mDetailShowEntity.title)
            assertEquals(dataMovie.director, mDetailShowEntity.director)
            assertEquals(dataMovie.genre, mDetailShowEntity.genre)
            assertEquals(dataMovie.quote, mDetailShowEntity.quote)
            assertEquals(dataMovie.score, mDetailShowEntity.score)
            assertEquals(dataMovie.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verify { observer.onChanged(dataMovie) }
        }
    }


    @Test
    fun fetchTvShows() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
            single<ShowRepository> { ShowRepositoryImpl(get()) }
        }) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = DetailViewModel(mShowRepository)

            val tvShow = MutableLiveData<DetailShowEntity>()
            tvShow.value = dataTvShow

            mDetailViewModel.setSelectedShow(tvShowId)
            mDetailViewModel.setSelectedShowType(DetailFragment.TYPE_MOVIE)

            every { mShowRepository.getMovieDetail(tvShowId) } returns tvShow
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value as DetailShowEntity
            verify { mShowRepository.getMovieDetail(tvShowId) }

            assertNotNull(mDetailShowEntity)
            assertEquals(dataTvShow.showId, mDetailShowEntity.showId)
            assertEquals(dataTvShow.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(dataTvShow.synopsis, mDetailShowEntity.synopsis)
            assertEquals(dataTvShow.posterPath, mDetailShowEntity.posterPath)
            assertEquals(dataTvShow.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(dataTvShow.title, mDetailShowEntity.title)
            assertEquals(dataTvShow.director, mDetailShowEntity.director)
            assertEquals(dataTvShow.genre, mDetailShowEntity.genre)
            assertEquals(dataTvShow.quote, mDetailShowEntity.quote)
            assertEquals(dataTvShow.score, mDetailShowEntity.score)
            assertEquals(dataTvShow.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verify { observer.onChanged(dataTvShow) }
        }
    }

    @Test
    fun fetchShowWithWrongId() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
            single<ShowRepository> { ShowRepositoryImpl(get()) }
        }) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = DetailViewModel(mShowRepository)

            val errorCode = "401"
            val statusCode = "34"
            val statusMessage = "The resource you requested could not be found."
            val errorMessage = getResourceString(R.string.error_message)
                .replace("%1\$d", errorCode)
                .replace("%2\$d", statusCode)
                .replace("%3\$s", statusMessage)

            val emptyDummyWithErrorMessage = DataDummy.generateEmptyShowDetail(errorMessage)
            val dummyId = emptyDummyWithErrorMessage.showId
            val show = MutableLiveData<DetailShowEntity>()
            show.value = emptyDummyWithErrorMessage

            mDetailViewModel.setSelectedShow(dummyId)
            mDetailViewModel.setSelectedShowType(DetailFragment.TYPE_MOVIE)

            every { mShowRepository.getMovieDetail(dummyId) } returns show
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value as DetailShowEntity
            verify { mShowRepository.getMovieDetail(dummyId) }

            assertNotNull(mDetailShowEntity)
            assertEquals(emptyDummyWithErrorMessage.showId, mDetailShowEntity.showId)
            assertEquals(emptyDummyWithErrorMessage.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(emptyDummyWithErrorMessage.synopsis, mDetailShowEntity.synopsis)
            assertEquals(emptyDummyWithErrorMessage.posterPath, mDetailShowEntity.posterPath)
            assertEquals(emptyDummyWithErrorMessage.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(emptyDummyWithErrorMessage.title, mDetailShowEntity.title)
            assertEquals(emptyDummyWithErrorMessage.director, mDetailShowEntity.director)
            assertEquals(emptyDummyWithErrorMessage.genre, mDetailShowEntity.genre)
            assertEquals(emptyDummyWithErrorMessage.quote, mDetailShowEntity.quote)
            assertEquals(emptyDummyWithErrorMessage.score, mDetailShowEntity.score)
            assertEquals(emptyDummyWithErrorMessage.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verify { observer.onChanged(emptyDummyWithErrorMessage) }
        }
    }

    @Test
    fun fetchShowWithWrongType() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
            single<ShowRepository> { ShowRepositoryImpl(get()) }
        }) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = spyk(DetailViewModel(mShowRepository))

            val type = "Film"
            val errorCode = "420"
            val statusCode = "69"
            val statusMessage = getResourceString(R.string.type_not_found_message)
            val errorMessage = getResourceString(R.string.error_message)
                .replace("%1\$d", errorCode)
                .replace("%2\$d", statusCode)
                .replace("%3\$s", statusMessage)

            val emptyDummyWithErrorMessage = DataDummy.generateEmptyShowDetail(errorMessage)
            val dummyId = emptyDummyWithErrorMessage.showId
            val show = MutableLiveData<DetailShowEntity>()
            show.value = emptyDummyWithErrorMessage

            mDetailViewModel.setSelectedShow(dummyId)
            mDetailViewModel.setSelectedShowType(type)

            every { mDetailViewModel.getShowDetail() } returns show
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value as DetailShowEntity
            verify { mShowRepository wasNot Called }

            assertNotNull(mDetailShowEntity)
            assertEquals(emptyDummyWithErrorMessage.showId, mDetailShowEntity.showId)
            assertEquals(emptyDummyWithErrorMessage.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(emptyDummyWithErrorMessage.synopsis, mDetailShowEntity.synopsis)
            assertEquals(emptyDummyWithErrorMessage.posterPath, mDetailShowEntity.posterPath)
            assertEquals(emptyDummyWithErrorMessage.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(emptyDummyWithErrorMessage.title, mDetailShowEntity.title)
            assertEquals(emptyDummyWithErrorMessage.director, mDetailShowEntity.director)
            assertEquals(emptyDummyWithErrorMessage.genre, mDetailShowEntity.genre)
            assertEquals(emptyDummyWithErrorMessage.quote, mDetailShowEntity.quote)
            assertEquals(emptyDummyWithErrorMessage.score, mDetailShowEntity.score)
            assertEquals(emptyDummyWithErrorMessage.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verify { observer.onChanged(emptyDummyWithErrorMessage) }
        }
    }
}