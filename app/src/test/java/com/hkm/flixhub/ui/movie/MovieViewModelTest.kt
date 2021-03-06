package com.hkm.flixhub.ui.movie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.test.core.app.ApplicationProvider
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.di.databaseModule
import com.hkm.flixhub.di.repositoryModule
import com.hkm.flixhub.utils.DataDummy
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
class MovieViewModelTest : KoinTest {
    private lateinit var mMovieViewModel: MovieViewModel
    private lateinit var mShowRepository: ShowRepository

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()
    private val observer: Observer<Resource<PagedList<ShowEntity>>> = mockk(relaxed = true)
    private val pagedList: PagedList<ShowEntity> = mockk(relaxed = true)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun getMovies() {
        app.loadModules(listOf(
            repositoryModule,
            databaseModule
        )) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mMovieViewModel = MovieViewModel(mShowRepository)

            pagedList.addAll(DataDummy.generateDummyMovies())
            val dummyData = Resource.success(pagedList)
            val movies = MutableLiveData<Resource<PagedList<ShowEntity>>>()
            movies.value = dummyData

            every { mShowRepository.getAllMovies() } returns movies
            val showEntities = mMovieViewModel.getMovies().value?.data
            verifyOrder { mShowRepository.getAllMovies() }

            assertNotNull(showEntities)
            assertEquals(dummyData.data?.size, showEntities?.size)

            mMovieViewModel.getMovies().observeForever(observer)
            verifyOrder { observer.onChanged(dummyData) }
        }

    }
}