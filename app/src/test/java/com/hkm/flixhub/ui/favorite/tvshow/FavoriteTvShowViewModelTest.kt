package com.hkm.flixhub.ui.favorite.tvshow

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
class FavoriteTvShowViewModelTest : KoinTest {
    private lateinit var mFavoriteTvShowViewModel: FavoriteTvShowViewModel
    private lateinit var mShowRepository: ShowRepository

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()
    private val observer: Observer<PagedList<ShowEntity>> = mockk(relaxed = true)
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
            mFavoriteTvShowViewModel = FavoriteTvShowViewModel(mShowRepository)

            pagedList.addAll(DataDummy.generateDummyTvShows())
            val dummyData = pagedList
            val movies = MutableLiveData<PagedList<ShowEntity>>()
            movies.value = dummyData

            every { mShowRepository.getFavoritedTvShows() } returns movies
            val showEntities = mFavoriteTvShowViewModel.getTvShows().value
            verifyOrder { mShowRepository.getFavoritedTvShows() }

            assertNotNull(showEntities)
            assertEquals(dummyData.size, showEntities?.size)

            mFavoriteTvShowViewModel.getTvShows().observeForever(observer)
            verifyOrder { observer.onChanged(dummyData) }
        }

    }
}