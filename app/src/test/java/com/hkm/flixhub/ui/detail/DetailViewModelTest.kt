package com.hkm.flixhub.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.agoda.kakao.common.utilities.getResourceString
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.R
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.di.databaseModule
import com.hkm.flixhub.di.repositoryModule
import com.hkm.flixhub.utils.DataDummy
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.vo.Resource
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 21, maxSdk = 28, application = FlixHubTest::class)
class DetailViewModelTest : KoinTest {
    private lateinit var mDetailViewModel: DetailViewModel
    private lateinit var mShowRepository: ShowRepository
    private val dataMovie = Resource.success(DataDummy.generateDummyMoviesDetail()[0])
    private val dataTvShow = Resource.success(DataDummy.generateDummyTvShowsDetail()[0])
    private val movieId = dataMovie.data?.showId as String
    private val tvShowId = dataTvShow.data?.showId as String

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()
    private val observer: Observer<Resource<ShowEntity>> = mockk(relaxed = true)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun fetchMovies() {
        app.loadModules(listOf(
            repositoryModule,
            databaseModule
        )) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = DetailViewModel(mShowRepository)

            val movie = MutableLiveData<Resource<ShowEntity>>()
            movie.value = dataMovie

            mDetailViewModel.setSelectedShow(movieId)
            mDetailViewModel.setSelectedShowType(ShowType.TYPE_MOVIE)

            every { mShowRepository.getMovieDetail(movieId) } returns movie
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value?.data as ShowEntity
            verifyOrder { mShowRepository.getMovieDetail(movieId) }

            assertNotNull(mDetailShowEntity)
            assertEquals(dataMovie.data?.showId, mDetailShowEntity.showId)
            assertEquals(dataMovie.data?.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(dataMovie.data?.synopsis, mDetailShowEntity.synopsis)
            assertEquals(dataMovie.data?.posterPath, mDetailShowEntity.posterPath)
            assertEquals(dataMovie.data?.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(dataMovie.data?.title, mDetailShowEntity.title)
            assertEquals(dataMovie.data?.director, mDetailShowEntity.director)
            assertEquals(dataMovie.data?.genre, mDetailShowEntity.genre)
            assertEquals(dataMovie.data?.quote, mDetailShowEntity.quote)
            assertEquals(dataMovie.data?.score, mDetailShowEntity.score)
            assertEquals(dataMovie.data?.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verifyOrder { observer.onChanged(dataMovie) }
        }
    }


    @Test
    fun fetchTvShows() {
        app.loadModules(listOf(
            repositoryModule,
            databaseModule
        )) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = DetailViewModel(mShowRepository)

            val tvShow = MutableLiveData<Resource<ShowEntity>>()
            tvShow.value = dataTvShow

            mDetailViewModel.setSelectedShow(tvShowId)
            mDetailViewModel.setSelectedShowType(ShowType.TYPE_MOVIE)

            every { mShowRepository.getMovieDetail(tvShowId) } returns tvShow
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value?.data as ShowEntity
            verifyOrder { mShowRepository.getMovieDetail(tvShowId) }

            assertNotNull(mDetailShowEntity)
            assertEquals(dataTvShow.data?.showId, mDetailShowEntity.showId)
            assertEquals(dataTvShow.data?.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(dataTvShow.data?.synopsis, mDetailShowEntity.synopsis)
            assertEquals(dataTvShow.data?.posterPath, mDetailShowEntity.posterPath)
            assertEquals(dataTvShow.data?.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(dataTvShow.data?.title, mDetailShowEntity.title)
            assertEquals(dataTvShow.data?.director, mDetailShowEntity.director)
            assertEquals(dataTvShow.data?.genre, mDetailShowEntity.genre)
            assertEquals(dataTvShow.data?.quote, mDetailShowEntity.quote)
            assertEquals(dataTvShow.data?.score, mDetailShowEntity.score)
            assertEquals(dataTvShow.data?.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verifyOrder { observer.onChanged(dataTvShow) }
        }
    }

    @Test
    fun fetchShowWithWrongId() {
        app.loadModules(listOf(
            repositoryModule,
            databaseModule
        )) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mDetailViewModel = DetailViewModel(mShowRepository)

            val errorCode = "401"
            val statusCode = "34"
            val statusMessage = "The resource you requested could not be found."
            val errorMessage = getResourceString(R.string.error_message)
                .replace("%1\$d", errorCode)
                .replace("%2\$d", statusCode)
                .replace("%3\$s", statusMessage)

            val errorDummy = Resource.success(DataDummy.generateEmptyShowDetail(errorMessage))
            val dummyId = errorDummy.data?.showId as String
            val show = MutableLiveData<Resource<ShowEntity>>()
            show.value = errorDummy

            mDetailViewModel.setSelectedShow(dummyId)
            mDetailViewModel.setSelectedShowType(ShowType.TYPE_MOVIE)

            every { mShowRepository.getMovieDetail(dummyId) } returns show
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value?.data as ShowEntity
            verify { mShowRepository.getMovieDetail(dummyId) }

            assertNotNull(mDetailShowEntity)
            assertEquals(errorDummy.data?.showId, mDetailShowEntity.showId)
            assertEquals(errorDummy.data?.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(errorDummy.data?.synopsis, mDetailShowEntity.synopsis)
            assertEquals(errorDummy.data?.posterPath, mDetailShowEntity.posterPath)
            assertEquals(errorDummy.data?.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(errorDummy.data?.title, mDetailShowEntity.title)
            assertEquals(errorDummy.data?.director, mDetailShowEntity.director)
            assertEquals(errorDummy.data?.genre, mDetailShowEntity.genre)
            assertEquals(errorDummy.data?.quote, mDetailShowEntity.quote)
            assertEquals(errorDummy.data?.score, mDetailShowEntity.score)
            assertEquals(errorDummy.data?.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verify { observer.onChanged(errorDummy) }
        }
    }

    @Test
    fun fetchShowWithWrongType() {
        app.loadModules(listOf(
            repositoryModule,
            databaseModule
        )) {
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

            val errorDummy = Resource.success(DataDummy.generateEmptyShowDetail(errorMessage))
            val dummyId = errorDummy.data?.showId as String
            val show = MutableLiveData<Resource<ShowEntity>>()
            show.value = errorDummy

            mDetailViewModel.setSelectedShow(dummyId)
            mDetailViewModel.setSelectedShowType(type)

            every { mDetailViewModel.getShowDetail() } returns show
            val mDetailShowEntity = mDetailViewModel.getShowDetail().value?.data as ShowEntity
            verify { mShowRepository wasNot Called }

            assertNotNull(mDetailShowEntity)
            assertEquals(errorDummy.data?.showId, mDetailShowEntity.showId)
            assertEquals(errorDummy.data?.releaseDate, mDetailShowEntity.releaseDate)
            assertEquals(errorDummy.data?.synopsis, mDetailShowEntity.synopsis)
            assertEquals(errorDummy.data?.posterPath, mDetailShowEntity.posterPath)
            assertEquals(errorDummy.data?.bannerPath, mDetailShowEntity.bannerPath)
            assertEquals(errorDummy.data?.title, mDetailShowEntity.title)
            assertEquals(errorDummy.data?.director, mDetailShowEntity.director)
            assertEquals(errorDummy.data?.genre, mDetailShowEntity.genre)
            assertEquals(errorDummy.data?.quote, mDetailShowEntity.quote)
            assertEquals(errorDummy.data?.score, mDetailShowEntity.score)
            assertEquals(errorDummy.data?.errorMessage, mDetailShowEntity.errorMessage)

            mDetailViewModel.getShowDetail().observeForever(observer)
            verify { observer.onChanged(errorDummy) }
        }
    }
}