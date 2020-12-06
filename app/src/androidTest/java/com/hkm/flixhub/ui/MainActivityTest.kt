package com.hkm.flixhub.ui

import android.content.Intent
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
import com.hkm.flixhub.ui.favorite.FavoriteScreen
import com.hkm.flixhub.ui.favorite.movie.FavoriteMovieScreen
import com.hkm.flixhub.ui.favorite.tvshow.FavoriteTvShowScreen
import com.hkm.flixhub.ui.home.HomeScreen
import com.hkm.flixhub.ui.movie.MovieScreen
import com.hkm.flixhub.ui.tvshow.TvShowScreen
import com.hkm.flixhub.utils.AppExecutors
import com.hkm.flixhub.utils.EspressoIdlingResource
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.SECOND_PAGE
import com.hkm.flixhub.utils.RecyclerViewTestUtil
import com.hkm.flixhub.utils.RecyclerViewTestUtil.Companion.atPosition
import com.hkm.flixhub.utils.SortUtils.DEFAULT
import com.hkm.flixhub.utils.SortUtils.ORIGINAL_TITLE
import com.hkm.flixhub.utils.SortUtils.POPULARITY
import com.hkm.flixhub.utils.SortUtils.SCORE
import com.hkm.flixhub.utils.SortUtils.SCORE_HIGHEST
import com.hkm.flixhub.utils.SortUtils.SCORE_LOWEST
import com.hkm.flixhub.utils.SortUtils.TITLE_ASC
import com.hkm.flixhub.utils.SortUtils.TITLE_DESC
import com.hkm.flixhub.utils.SortUtils.VOTE_COUNT
import com.hkm.flixhub.vo.Status
import junit.framework.AssertionFailedError
import org.junit.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class MainActivityTest : KoinTest {
    private var dataMovie: PagedList<ShowEntity>? = null
    private var dataTvShow: PagedList<ShowEntity>? = null
    private var dataMovieDetail = ArrayList<ShowEntity>()
    private var dataTvShowDetail = ArrayList<ShowEntity>()
    private var dataFavoriteMovie: PagedList<ShowEntity>? = null
    private var dataFavoriteTvShow: PagedList<ShowEntity>? = null
    private var movieId = ArrayList<String>()
    private var tvShowId = ArrayList<String>()
    private val pageMovie = MutableLiveData<String>()
    private val pageTvShow = MutableLiveData<String>()
    private val sortMovie = MutableLiveData<String>()
    private val sortTvShow = MutableLiveData<String>()
    private val sortFavoriteMovie = MutableLiveData<String>()
    private val sortFavoriteTvShow = MutableLiveData<String>()
    private var testTag = "null"

    private val apiSort = listOf(ORIGINAL_TITLE, SCORE, VOTE_COUNT, POPULARITY)
    private val apiSortString = listOf(
        R.string.menu_original_title,
        R.string.menu_score,
        R.string.menu_vote_count,
        R.string.menu_popularity
    )

    private val favoriteSort = listOf(TITLE_ASC, TITLE_DESC, SCORE_HIGHEST, SCORE_LOWEST, DEFAULT)
    private val favoriteSortString = listOf(
        R.string.menu_title_a_z,
        R.string.menu_title_z_a,
        R.string.menu_score_highest,
        R.string.menu_score_lowest,
        R.string.menu_default
    )

    private val app: FlixHubInstrumentedTest = ApplicationProvider.getApplicationContext()

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        testTag = "null"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())
        Intents.init()
    }

    @After
    fun tearDown() {
        testTag = "null"
        dataFavoriteMovie = null
        dataFavoriteTvShow = null
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

                onView(withText(getResourceString(R.string.movie))).perform(click())
            }

            for (sortIndex in apiSort.indices) {
                onScreen<MovieScreen> {
                    rvMovie {
                        with(onView(withId(R.id.rv_movie))) {
                            while (true) {
                                try {
                                    check(matches(ViewMatchers.isDisplayed()))
                                    check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                                    this@onScreen.progressBar.isGone()
                                    break
                                } catch (e: AssertionFailedError) {
                                    idle(250)
                                }
                            }

                            for (index in 0 until 40) {
                                if (index == 19) {
                                    pageMovie.postValue(SECOND_PAGE)
                                    while ((dataMovie?.size as Int) < 40) {
                                        idle(250)
                                    }

                                    check(RecyclerViewTestUtil.hasSize(dataMovie?.size as Int))
                                }

                                perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                    index))

                                while (true) {
                                    try {
                                        check(matches(atPosition(index,
                                            hasDescendant(withText(dataMovie?.get(index)?.title)))))
                                        break
                                    } catch (e: AssertionFailedError) {
                                        idle(250)
                                    }
                                }
                            }
                            pageMovie.postValue(FIRST_PAGE)
                        }
                    }
                }

                onScreen<MovieScreen> {
                    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                    onView(withText(getResourceString(apiSortString[sortIndex]))).perform(click())
                        .also {
                            sortMovie.postValue(apiSort[sortIndex])
                        }

                    while (dataMovie == null) {
                        idle(250)
                    }
                }
            }
        }
    }

    @Test
    fun loadDetailMovie() {
        testTag = "detail_movie"

        startTest(app) {
            val movieDetail = dataMovieDetail[0]

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
                            hasDescendant(withText(movieDetail.title)))))
                        perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            ViewActions.click()))
                    }
                }
            }

            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(movieDetail.title)
                }
            }

            onScreen<DetailScreen> {
                progressBar {
                    isGone()
                }
                tvTitle {
                    isDisplayed()
                    hasText(movieDetail.title)
                }
                tvDirector {
                    isDisplayed()
                    hasText(movieDetail.director)
                }
                tvGenre {
                    isDisplayed()
                    hasText(movieDetail.genre)
                }
                tvQuote {
                    isDisplayed()
                    hasText(movieDetail.quote)
                }
                tvScore {
                    isDisplayed()
                    hasText(movieDetail.score)
                }
                tvDate {
                    isDisplayed()
                    hasText(movieDetail.releaseDate)
                }
                tvSynopsis {
                    isDisplayed()
                    hasText(movieDetail.synopsis)
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
    fun loadFavoriteMovie() {
        testTag = "favorite_movie"

        startTest(app) {
            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.app_name)
                }
            }

            onScreen<HomeScreen> {
                sortFavoriteMovie.postValue(DEFAULT)

                while (dataFavoriteMovie == null) {
                    idle(250)
                }

                onView(withId(R.id.menu_favorite)).perform(click())
            }

            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.menu_favorite)
                }
            }

            onScreen<FavoriteScreen> {
                sortFavoriteMovie.postValue(DEFAULT)

                onView(withId(R.id.menu_delete_all)).perform(click())
                onView(withText(R.string.dialog_confirm_yes)).perform(click())
            }

            onScreen<FavoriteMovieScreen> {
                progressBar.isGone()
                tvFavoriteMovieNotFound.isDisplayed()
                rvFavoriteMovie.isGone()
                pressBack()
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

            for (index in 0 until 6) {
                onScreen<MovieScreen> {
                    rvMovie {
                        with(onView(withId(R.id.rv_movie))) {
                            this@onScreen.progressBar.isGone()
                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                                index,
                                ViewActions.click()))
                        }
                    }
                }

                onScreen<DetailScreen> {
                    progressBar {
                        isGone()
                    }
                    btnFavorite {
                        click()
                    }

                    pressBack()
                }
            }

            onScreen<HomeScreen> {
                sortFavoriteMovie.postValue(DEFAULT)

                while (dataFavoriteMovie == null) {
                    idle(250)
                }

                onView(withId(R.id.menu_favorite)).perform(click())
            }

            onScreen<FavoriteScreen> {
                tabsFavorite {
                    isDisplayed()
                    isTabSelected(0)
                }
                viewPagerFavorite {
                    isDisplayed()
                    isAtPage(0)
                }

                onView(withText(getResourceString(R.string.movie))).perform(click())
            }

            onScreen<FavoriteMovieScreen> {
                progressBar.isGone()
                rvFavoriteMovie {
                    isDisplayed()
                    hasSize(dataFavoriteMovie?.size as Int)
                }
            }

            for (sortIndex in favoriteSort.indices) {
                onScreen<FavoriteMovieScreen> {
                    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                    onView(withText(getResourceString(favoriteSortString[sortIndex]))).perform(click())
                        .also {
                            dataFavoriteMovie = null
                            sortFavoriteMovie.postValue(favoriteSort[sortIndex])
                        }

                    while (dataFavoriteMovie == null) {
                        idle(250)
                    }
                }

                onScreen<FavoriteMovieScreen> {
                    rvFavoriteMovie {
                        with(onView(withId(R.id.rv_favorite_movie))) {
                            check(matches(ViewMatchers.isDisplayed()))
                            check(RecyclerViewTestUtil.hasSize(dataFavoriteMovie?.size as Int))

                            for ((index, movie) in (dataFavoriteMovie as PagedList<ShowEntity>).withIndex()) {
                                perform(RecyclerViewActions
                                    .scrollToPosition<RecyclerView.ViewHolder>(index))
                                check(matches(atPosition(index,
                                    hasDescendant(withText(movie.title)))))
                            }
                        }
                    }
                }
            }

            onScreen<FavoriteMovieScreen> {
                rvFavoriteMovie {
                    with(onView(withId(R.id.rv_favorite_movie))) {
                        this@onScreen.progressBar.isGone()
                        perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                            0))
                        check(matches(atPosition(0,
                            hasDescendant(withText(dataFavoriteMovie?.get(0)?.title)))))
                        perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            ViewActions.click()))
                    }
                }
            }

            onScreen<DetailScreen> {
                progressBar.isGone()
                btnFavorite.click()
                pressBack()
            }

            onScreen<FavoriteMovieScreen> {
                progressBar.isGone()
                rvFavoriteMovie {
                    isDisplayed()
                    hasSize(dataFavoriteMovie?.size as Int)
                }
            }

            onScreen<FavoriteScreen> {
                sortFavoriteMovie.postValue(DEFAULT)

                onView(withId(R.id.menu_delete_all)).perform(click())
                onView(withText(R.string.dialog_confirm_yes)).perform(click())
            }

            onScreen<FavoriteMovieScreen> {
                progressBar.isGone()
                rvFavoriteMovie.isGone()
                tvFavoriteMovieNotFound.isDisplayed()
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

                onView(withText(getResourceString(R.string.tv_show))).perform(click())
            }

            for (sortIndex in apiSort.indices) {
                onScreen<TvShowScreen> {
                    rvTvShow {
                        with(onView(withId(R.id.rv_tv_show))) {
                            while (true) {
                                try {
                                    check(matches(ViewMatchers.isDisplayed()))
                                    check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                                    this@onScreen.progressBar.isGone()
                                    break
                                } catch (e: AssertionFailedError) {
                                    idle(250)
                                }
                            }

                            for (index in 0 until 40) {
                                if (index == 19) {
                                    pageTvShow.postValue(SECOND_PAGE)
                                    while ((dataTvShow?.size as Int) < 40) {
                                        idle(250)
                                    }

                                    check(RecyclerViewTestUtil.hasSize(dataTvShow?.size as Int))
                                }

                                perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                    index))


                                while (true) {
                                    try {
                                        check(matches(atPosition(index,
                                            hasDescendant(withText(dataTvShow?.get(index)?.title)))))
                                        break
                                    } catch (e: AssertionFailedError) {
                                        idle(250)
                                    }
                                }
                            }

                            pageTvShow.postValue(FIRST_PAGE)
                        }
                    }
                }

                onScreen<TvShowScreen> {
                    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                    onView(withText(getResourceString(apiSortString[sortIndex]))).perform(click())
                        .also {
                            sortTvShow.postValue(apiSort[sortIndex])
                        }

                    while (dataTvShow == null) {
                        idle(250)
                    }
                }
            }
        }
    }

    @Test
    fun loadDetailTvShow() {
        testTag = "detail_tv_show"

        startTest(app) {
            val tvShowDetail = dataTvShowDetail[0]

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
                            hasDescendant(withText(tvShowDetail.title)))))
                        perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            ViewActions.click()))
                    }
                }
            }

            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(tvShowDetail.title)
                }
            }

            onScreen<DetailScreen> {
                progressBar {
                    isGone()
                }
                tvTitle {
                    isDisplayed()
                    hasText(tvShowDetail.title)
                }
                tvDirector {
                    isDisplayed()
                    hasText(tvShowDetail.director)
                }
                tvGenre {
                    isDisplayed()
                    hasText(tvShowDetail.genre)
                }
                tvQuote {
                    isDisplayed()
                    hasText(tvShowDetail.quote)
                }
                tvScore {
                    isDisplayed()
                    hasText(tvShowDetail.score)
                }
                tvDate {
                    isDisplayed()
                    hasText(tvShowDetail.releaseDate)
                }
                tvSynopsis {
                    isDisplayed()
                    hasText(tvShowDetail.synopsis)
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
    fun loadFavoriteTvShow() {
        testTag = "favorite_tv_show"

        startTest(app) {
            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.app_name)
                }
            }

            onScreen<HomeScreen> {
                sortFavoriteTvShow.postValue(DEFAULT)

                while (dataFavoriteTvShow == null) {
                    idle(250)
                }

                onView(withId(R.id.menu_favorite)).perform(click())
            }

            onScreen<MainScreen> {
                toolbar {
                    isDisplayed()
                    hasTitle(R.string.menu_favorite)
                }
            }

            onScreen<FavoriteScreen> {
                tabsFavorite {
                    isDisplayed()
                    isTabSelected(0)
                    selectTab(1)
                    isTabSelected(1)
                }

                viewPagerFavorite {
                    isDisplayed()
                    swipeLeft()
                    isAtPage(1)
                }

                onView(withText(getResourceString(R.string.tv_show))).perform(click())

                sortFavoriteTvShow.postValue(DEFAULT)

                onView(withId(R.id.menu_delete_all)).perform(click())
                onView(withText(R.string.dialog_confirm_yes)).perform(click())
            }

            onScreen<FavoriteTvShowScreen> {
                progressBar.isGone()
                tvFavoriteTvShowNotFound.isDisplayed()
                rvFavoriteTvShow.isGone()
                pressBack()
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

                onView(withText(getResourceString(R.string.tv_show))).perform(click())
            }

            for (index in 0 until 6) {
                onScreen<TvShowScreen> {
                    rvTvShow {
                        with(onView(withId(R.id.rv_tv_show))) {
                            this@onScreen.progressBar.isGone()
                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                                index,
                                ViewActions.click()))
                        }
                    }
                }

                onScreen<DetailScreen> {
                    progressBar {
                        isGone()
                    }
                    btnFavorite {
                        click()
                    }

                    pressBack()
                }
            }

            onScreen<HomeScreen> {
                sortFavoriteTvShow.postValue(DEFAULT)

                while (dataFavoriteTvShow == null) {
                    idle(250)
                }

                onView(withId(R.id.menu_favorite)).perform(click())
            }

            onScreen<FavoriteScreen> {
                tabsFavorite {
                    isDisplayed()
                    isTabSelected(0)
                    selectTab(1)
                    isTabSelected(1)
                }

                viewPagerFavorite {
                    isDisplayed()
                    swipeLeft()
                    isAtPage(1)
                }

                onView(withText(getResourceString(R.string.tv_show))).perform(click())
            }

            onScreen<FavoriteTvShowScreen> {
                progressBar.isGone()
                rvFavoriteTvShow {
                    isDisplayed()
                    hasSize(dataFavoriteTvShow?.size as Int)
                }
            }

            for (sortIndex in favoriteSort.indices) {
                onScreen<FavoriteTvShowScreen> {
                    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                    onView(withText(getResourceString(favoriteSortString[sortIndex]))).perform(click())
                        .also {
                            dataFavoriteTvShow = null
                            sortFavoriteTvShow.postValue(favoriteSort[sortIndex])
                        }

                    while (dataFavoriteTvShow == null) {
                        idle(250)
                    }
                }

                onScreen<FavoriteTvShowScreen> {
                    rvFavoriteTvShow {
                        with(onView(withId(R.id.rv_favorite_tv_show))) {
                            check(matches(ViewMatchers.isDisplayed()))
                            check(RecyclerViewTestUtil.hasSize(dataFavoriteTvShow?.size as Int))

                            for ((index, tvShow) in (dataFavoriteTvShow as PagedList<ShowEntity>).withIndex()) {
                                perform(RecyclerViewActions
                                    .scrollToPosition<RecyclerView.ViewHolder>(index))
                                check(matches(atPosition(index,
                                    hasDescendant(withText(tvShow.title)))))
                            }
                        }
                    }
                }
            }

            onScreen<FavoriteTvShowScreen> {
                rvFavoriteTvShow {
                    with(onView(withId(R.id.rv_favorite_tv_show))) {
                        this@onScreen.progressBar.isGone()
                        perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                            0))
                        check(matches(atPosition(0,
                            hasDescendant(withText(dataFavoriteTvShow?.get(0)?.title)))))
                        perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                            0,
                            ViewActions.click()))
                    }
                }
            }

            onScreen<DetailScreen> {
                progressBar.isGone()
                btnFavorite.click()
                pressBack()
            }

            onScreen<FavoriteTvShowScreen> {
                progressBar.isGone()
                rvFavoriteTvShow {
                    isDisplayed()
                    hasSize(dataFavoriteTvShow?.size as Int)
                }
            }

            onScreen<FavoriteScreen> {
                sortFavoriteTvShow.postValue(DEFAULT)

                onView(withId(R.id.menu_delete_all)).perform(click())
                onView(withText(R.string.dialog_confirm_yes)).perform(click())
            }

            onScreen<FavoriteTvShowScreen> {
                progressBar.isGone()
                rvFavoriteTvShow.isGone()
                tvFavoriteTvShowNotFound.isDisplayed()
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

            activityRule.scenario.onActivity {
                Transformations.switchMap(sortMovie) { sort ->
                    Transformations.switchMap(pageMovie) { page ->
                        mFakeShowRepository.getAllMovies(sort, page)
                    }
                }.observe(it, { movie ->
                    when (movie.status) {
                        Status.SUCCESS -> {
                            dataMovie = movie.data as PagedList<ShowEntity>
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
                        }
                        else -> {
                        }
                    }
                })
            }

            while (dataMovie == null || dataTvShow == null) {
                idle(250)
            }

            for (index in 0 until 6) {
                movieId.add(dataMovie?.get(index)?.showId.toString())
                tvShowId.add(dataTvShow?.get(index)?.showId.toString())
            }

            if (testTag != "null") {
                for (index in 0 until movieId.size) {
                    val movieDetails = mFakeShowRepository.getMovieDetail(movieId[index])
                    val tvShowDetails = mFakeShowRepository.getTvShowDetail(tvShowId[index])

                    activityRule.scenario.onActivity {
                        movieDetails.observe(it, { movie ->
                            when (movie.status) {
                                Status.SUCCESS -> {
                                    dataMovieDetail.add(movie.data as ShowEntity)
                                }
                                else -> {
                                }
                            }
                        })
                        tvShowDetails.observe(it, { tvShow ->
                            when (tvShow.status) {
                                Status.SUCCESS -> {
                                    dataTvShowDetail.add(tvShow.data as ShowEntity)
                                }
                                else -> {
                                }
                            }
                        })
                    }
                }

                while (dataMovieDetail.size < 6 && dataTvShowDetail.size < 6) {
                    idle(250)
                }

                activityRule.scenario.onActivity {
                    Transformations.switchMap(sortFavoriteMovie) { sort ->
                        mFakeShowRepository.getFavoritedMovies(sort)
                    }.observe(it, { movie -> dataFavoriteMovie = movie })
                    Transformations.switchMap(sortFavoriteTvShow) { sort ->
                        mFakeShowRepository.getFavoritedTvShows(sort)
                    }.observe(it, { tvShow -> dataFavoriteTvShow = tvShow })
                }
            }


            block()
        }
    }
}