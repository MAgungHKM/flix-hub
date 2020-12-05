package com.hkm.flixhub.ui.tvshow

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
class TvShowViewModelTest : KoinTest {
    private lateinit var mTvShowViewModel: TvShowViewModel
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
    fun getTvShows() {
        app.loadModules(listOf(
            repositoryModule,
            databaseModule
        )) {
            mShowRepository = spyk(get<ShowRepository>(), recordPrivateCalls = true)
            mTvShowViewModel = TvShowViewModel(mShowRepository)

            pagedList.addAll(DataDummy.generateDummyMovies())
            val dummyData = Resource.success(pagedList)
            val tvShows = MutableLiveData<Resource<PagedList<ShowEntity>>>()
            tvShows.value = dummyData

            every { mShowRepository.getAllTvShows() } returns tvShows
            val showEntities = mTvShowViewModel.getTvShows().value?.data
            verifyOrder { mShowRepository.getAllTvShows() }

            assertNotNull(showEntities)
            assertEquals(dummyData.data?.size, showEntities?.size)

            mTvShowViewModel.getTvShows().observeForever(observer)
            verifyOrder { observer.onChanged(dummyData) }
        }

    }
}