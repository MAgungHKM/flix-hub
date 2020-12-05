package com.hkm.flixhub.ui

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.agoda.kakao.common.utilities.getResourceString
import com.agoda.kakao.screen.Screen.Companion.idle
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.hkm.flixhub.FlixHubInstrumentedTest
import com.hkm.flixhub.R
import com.hkm.flixhub.data.FakeShowRepository
import com.hkm.flixhub.data.FakeShowRepositoryImpl
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.ShowRepositoryImpl
import com.hkm.flixhub.data.source.local.LocalDataSource
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.di.databaseModule
import com.hkm.flixhub.di.viewModelModule
import com.hkm.flixhub.ui.detail.DetailScreen
import com.hkm.flixhub.ui.home.HomeScreen
import com.hkm.flixhub.ui.movie.MovieScreen
import com.hkm.flixhub.ui.tvshow.TvShowScreen
import com.hkm.flixhub.utils.AppExecutors
import com.hkm.flixhub.utils.EspressoIdlingResource
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.SECOND_PAGE
import com.hkm.flixhub.utils.RecyclerViewTestUtil
import com.hkm.flixhub.utils.RecyclerViewTestUtil.Companion.atPosition
import com.hkm.flixhub.utils.SortUtils.ORIGINAL_TITLE
import com.hkm.flixhub.utils.SortUtils.POPULARITY
import com.hkm.flixhub.utils.SortUtils.SCORE
import com.hkm.flixhub.utils.SortUtils.VOTE_COUNT
import com.hkm.flixhub.vo.Resource
import com.hkm.flixhub.vo.Status
import org.junit.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class MainActivityTest : KoinTest {
    private lateinit var movieLiveData: LiveData<Resource<PagedList<ShowEntity>>>
    private lateinit var tvShowLiveData: LiveData<Resource<PagedList<ShowEntity>>>
    private var dataMovie: PagedList<ShowEntity>? = null
    private var dataTvShow: PagedList<ShowEntity>? = null
    private var dataMovieDetail: ShowEntity? = null
    private var dataTvShowDetail: ShowEntity? = null
    private var movieId: String? = null
    private var tvShowId: String? = null
    private val pageMovie = MutableLiveData<String>()
    private val pageTvShow = MutableLiveData<String>()
    private val sortMovie = MutableLiveData<String>()
    private val sortTvShow = MutableLiveData<String>()

    private val app: FlixHubInstrumentedTest = ApplicationProvider.getApplicationContext()

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource())
        Intents.release()
    }

    @Test
    fun loadMovies() {
        startTest(app) {
            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.app_name)
                }
            }

            onScreen<HomeScreen> {
                tabs {
                    isDisplayed()
                    isTabSelected(0)
                }
                viewPager {
                    isDisplayed()
                    isAtPage(0)
                }
            }

            onScreen<MovieScreen> {
                rvMovie {
                    with(onView(withId(R.id.rv_movie))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageMovie.postValue(SECOND_PAGE)
                                while ((dataMovie?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataMovie?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataMovie?.get(index)?.title)))))
                        }
                        pageMovie.postValue(FIRST_PAGE)
                    }
                }
            }

            onScreen<MovieScreen> {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(getResourceString(R.string.menu_original_title))).perform(click())
                    .also {
                        sortMovie.postValue(ORIGINAL_TITLE)
                    }

                while (dataMovie == null) {
                    idle(250)
                }
            }

            onScreen<MovieScreen> {
                rvMovie {
                    with(onView(withId(R.id.rv_movie))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageMovie.postValue(SECOND_PAGE)
                                while ((dataMovie?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataMovie?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataMovie?.get(index)?.title)))))
                        }
                        pageMovie.postValue(FIRST_PAGE)
                    }
                }
            }

            onScreen<MovieScreen> {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(getResourceString(R.string.menu_score))).perform(click()).also {
                    sortMovie.postValue(SCORE)
                }

                while (dataMovie == null) {
                    idle(250)
                }
            }

            onScreen<MovieScreen> {
                rvMovie {
                    with(onView(withId(R.id.rv_movie))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageMovie.postValue(SECOND_PAGE)
                                while ((dataMovie?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataMovie?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataMovie?.get(index)?.title)))))
                        }
                        pageMovie.postValue(FIRST_PAGE)
                    }
                }
            }

            onScreen<MovieScreen> {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(getResourceString(R.string.menu_vote_count))).perform(click())
                    .also {
                        sortMovie.postValue(VOTE_COUNT)
                    }

                while (dataMovie == null) {
                    idle(250)
                }
            }

            onScreen<MovieScreen> {
                rvMovie {
                    with(onView(withId(R.id.rv_movie))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageMovie.postValue(SECOND_PAGE)
                                while ((dataMovie?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataMovie?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataMovie?.get(index)?.title)))))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun loadDetailMovie() {
        startTest(app) {
            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.app_name)
                }
            }

            onScreen<HomeScreen> {
                tabs {
                    isDisplayed()
                    isTabSelected(0)
                }
                viewPager {
                    isDisplayed()
                    isAtPage(0)
                }

                onView(withText(getResourceString(R.string.movie))).perform(click())
            }

            onScreen<MovieScreen> {
                rvMovie {
                    with(onView(withId(R.id.rv_movie))) {
                        this@onScreen.progressBar.isGone()
                        check(matches(atPosition(0,
                            hasDescendant(withText(dataMovieDetail?.title)))))
                        perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            ViewActions.click()))
                    }
                }
            }

            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(dataMovieDetail?.title.toString())
                }
            }

            onScreen<DetailScreen> {
                progressBar {
                    isGone()
                }
                tvTitle {
                    isDisplayed()
                    hasText(dataMovieDetail?.title.toString())
                }
                tvDirector {
                    isDisplayed()
                    hasText(dataMovieDetail?.director.toString())
                }
                tvGenre {
                    isDisplayed()
                    hasText(dataMovieDetail?.genre.toString())
                }
                tvQuote {
                    isDisplayed()
                    hasText(dataMovieDetail?.quote.toString())
                }
                tvScore {
                    isDisplayed()
                    hasText(dataMovieDetail?.score.toString())
                }
                tvDate {
                    isDisplayed()
                    hasText(dataMovieDetail?.releaseDate.toString())
                }
                tvSynopsis {
                    isDisplayed()
                    hasText(dataMovieDetail?.synopsis.toString())
                }
                imgPoster {
                    isDisplayed()

                }
                imgBanner {
                    isDisplayed()
                }
                btnShare {
                    click()
                    intended(hasAction(Intent.ACTION_CHOOSER))
                    intended(hasExtra(Intent.EXTRA_TITLE, getResourceString(R.string.share_title)))
                }
            }
        }
    }

    @Test
    fun loadTvShows() {
        startTest(app) {
            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.app_name)
                }
            }

            onScreen<HomeScreen> {
                tabs {
                    isDisplayed()
                    isTabSelected(0)
                    selectTab(1)
                    isTabSelected(1)
                }
                viewPager {
                    isDisplayed()
                    swipeLeft()
                    isAtPage(1)
                }
            }

            onScreen<TvShowScreen> {
                rvTvShow {
                    with(onView(withId(R.id.rv_tv_show))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageTvShow.postValue(SECOND_PAGE)
                                while ((dataTvShow?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataTvShow?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataTvShow?.get(index)?.title)))))
                        }
                        pageTvShow.postValue(FIRST_PAGE)
                    }
                }
            }

            onScreen<TvShowScreen> {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(getResourceString(R.string.menu_original_title))).perform(click())
                    .also {
                        sortTvShow.postValue(ORIGINAL_TITLE)
                    }

                while (dataTvShow == null) {
                    idle(250)
                }
            }

            onScreen<TvShowScreen> {
                rvTvShow {
                    with(onView(withId(R.id.rv_tv_show))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageTvShow.postValue(SECOND_PAGE)
                                while ((dataTvShow?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataTvShow?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataTvShow?.get(index)?.title)))))
                        }
                        pageTvShow.postValue(FIRST_PAGE)
                    }
                }
            }

            onScreen<TvShowScreen> {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(getResourceString(R.string.menu_score))).perform(click()).also {
                    sortTvShow.postValue(SCORE)
                }

                while (dataTvShow == null) {
                    idle(250)
                }
            }

            onScreen<TvShowScreen> {
                rvTvShow {
                    with(onView(withId(R.id.rv_tv_show))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageTvShow.postValue(SECOND_PAGE)
                                while ((dataTvShow?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataTvShow?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataTvShow?.get(index)?.title)))))
                        }
                        pageTvShow.postValue(FIRST_PAGE)
                    }
                }
            }

            onScreen<TvShowScreen> {
                openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                onView(withText(getResourceString(R.string.menu_vote_count))).perform(click())
                    .also {
                        sortTvShow.postValue(VOTE_COUNT)
                    }

                while (dataTvShow == null) {
                    idle(250)
                }
            }

            onScreen<TvShowScreen> {
                rvTvShow {
                    with(onView(withId(R.id.rv_tv_show))) {
                        check(matches(ViewMatchers.isDisplayed()))
                        check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                        this@onScreen.progressBar.isGone()

                        for (index in 0 until 40) {
                            if (index == 19) {
                                pageTvShow.postValue(SECOND_PAGE)
                                while ((dataTvShow?.size as Int) < 40) {
                                    idle(250)
                                }

                                Log.wtf("SIZE", dataTvShow?.size.toString())

                                check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                            }

                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index,
                                hasDescendant(withText(dataTvShow?.get(index)?.title)))))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun loadDetailTvShow() {
        startTest(app) {
            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.app_name)
                }
            }

            onScreen<HomeScreen> {
                tabs {
                    isDisplayed()
                    isTabSelected(0)
                    selectTab(1)
                    isTabSelected(1)
                }
                viewPager {
                    isDisplayed()
                    isAtPage(1)
                }

                onView(withText(getResourceString(R.string.tv_show))).perform(click())
            }

            onScreen<TvShowScreen> {
                rvTvShow {
                    with(onView(withId(R.id.rv_tv_show))) {
                        this@onScreen.progressBar.isGone()
                        check(matches(atPosition(0,
                            hasDescendant(withText(dataTvShowDetail?.title)))))
                        perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            ViewActions.click()))
                    }
                }
            }

            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(dataTvShowDetail?.title.toString())
                }
            }

            onScreen<DetailScreen> {
                progressBar {
                    isGone()
                }
                tvTitle {
                    isDisplayed()
                    hasText(dataTvShowDetail?.title.toString())
                }
                tvDirector {
                    isDisplayed()
                    hasText(dataTvShowDetail?.director.toString())
                }
                tvGenre {
                    isDisplayed()
                    hasText(dataTvShowDetail?.genre.toString())
                }
                tvQuote {
                    isDisplayed()
                    hasText(dataTvShowDetail?.quote.toString())
                }
                tvScore {
                    isDisplayed()
                    hasText(dataTvShowDetail?.score.toString())
                }
                tvDate {
                    isDisplayed()
                    hasText(dataTvShowDetail?.releaseDate.toString())
                }
                tvSynopsis {
                    isDisplayed()
                    hasText(dataTvShowDetail?.synopsis.toString())
                }
                imgPoster {
                    isDisplayed()
                }
                imgBanner {
                    isDisplayed()
                }
                btnShare {
                    click()
                    intended(hasAction(Intent.ACTION_CHOOSER))
                    intended(hasExtra(Intent.EXTRA_TITLE, getResourceString(R.string.share_title)))
                }
            }
        }
    }

    private fun startTest(app: FlixHubInstrumentedTest, block: () -> Unit) {
        app.loadModules(listOf(
            module {
                single { RemoteDataSource(androidContext()) }
                single { LocalDataSource(get()) }
                single { AppExecutors() }
                single<ShowRepository> { ShowRepositoryImpl(get(), get(), get()) }
                single<FakeShowRepository> { FakeShowRepositoryImpl(get(), get(), get()) }
            },
            viewModelModule,
            databaseModule
        )) {
            val mFakeShowRepository: FakeShowRepository by inject()

            pageMovie.postValue(FIRST_PAGE)
            pageTvShow.postValue(FIRST_PAGE)
            sortMovie.postValue(POPULARITY)
            sortTvShow.postValue(POPULARITY)

            movieLiveData = mFakeShowRepository.getAllMovies()
            tvShowLiveData = mFakeShowRepository.getAllTvShows()
            activityRule.scenario.onActivity {
                Transformations.switchMap(sortMovie) { sort ->
                    Transformations.switchMap(pageMovie) { page ->
                        mFakeShowRepository.getAllMovies(sort, page)
                    }
                }.observe(it, { movie ->
                    when (movie.status) {
                        Status.SUCCESS -> {
                            dataMovie = movie.data as PagedList<ShowEntity>
                            movieId = movie.data?.get(0)?.showId
                        }
                        else -> {
                        }
                    }
                })
                Transformations.switchMap(sortTvShow) { sort ->
                    Transformations.switchMap(pageTvShow) { page ->
                        mFakeShowRepository.getAllTvShows(sort, page)
                    }
                }.observe(it, { tvShow ->
                    when (tvShow.status) {
                        Status.SUCCESS -> {
                            dataTvShow = tvShow.data as PagedList<ShowEntity>
                            tvShowId = tvShow.data?.get(0)?.showId
                        }
                        else -> {
                        }
                    }
                })
            }

            while (dataMovie == null || dataTvShow == null) {
                idle(250)
            }

            val movieDetails = mFakeShowRepository.getMovieDetail(movieId.toString())
            val tvShowDetails = mFakeShowRepository.getTvShowDetail(tvShowId.toString())

            activityRule.scenario.onActivity {
                movieDetails.observe(it, { movie ->
                    when (movie.status) {
                        Status.SUCCESS -> {
                            dataMovieDetail = movie.data
                        }
                        else -> {
                        }
                    }
                })
                tvShowDetails.observe(it, { tvShow ->
                    when (tvShow.status) {
                        Status.SUCCESS -> {
                            dataTvShowDetail = tvShow.data
                        }
                        else -> {
                        }
                    }
                })
            }

            while (true) {
                if (dataMovieDetail != null && dataTvShowDetail != null)
                    break

                idle(250)
            }

            block()
        }
    }
}