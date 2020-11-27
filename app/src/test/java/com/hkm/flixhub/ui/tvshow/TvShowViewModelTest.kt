package com.hkm.flixhub.ui.tvshow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.ShowRepositoryImpl
import com.hkm.flixhub.data.source.local.entity.ShowEntity
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
class TvShowViewModelTest : KoinTest {
    private lateinit var mTvShowViewModel: TvShowViewModel
    private lateinit var mShowRepository: ShowRepository

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()
    private val observer: Observer<ArrayList<ShowEntity>> = mockk(relaxed = true)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun getMovies() {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
            single<ShowRepository> { ShowRepositoryImpl(get()) }
        }) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mTvShowViewModel = TvShowViewModel(mShowRepository)

            val data = DataDummy.generateDummyTvShows()
            val tvShows = MutableLiveData<ArrayList<ShowEntity>>()
            tvShows.value = data

            every { mShowRepository.getAllTvShows() } returns tvShows
            val showEntities = mTvShowViewModel.getTvShows().value
            verifyOrder { mShowRepository.getAllTvShows() }

            assertNotNull(showEntities)
            assertEquals(data.size, showEntities?.size)

            mTvShowViewModel.getTvShows().observeForever(observer)
            verifyOrder { observer.onChanged(data) }
        }

    }
}