package com.hkm.flixhub.ui

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.agoda.kakao.common.utilities.getResourceString
import com.agoda.kakao.screen.Screen.Companion.idle
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.hkm.flixhub.FlixHubInstrumentedTest
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.FakeShowRepository
import com.hkm.flixhub.data.source.FakeShowRepositoryImpl
import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.ShowRepositoryImpl
import com.hkm.flixhub.data.source.local.entity.DetailShowEntity
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.ui.detail.DetailScreen
import com.hkm.flixhub.ui.detail.DetailViewModel
import com.hkm.flixhub.ui.home.HomeScreen
import com.hkm.flixhub.ui.movie.MovieScreen
import com.hkm.flixhub.ui.movie.MovieViewModel
import com.hkm.flixhub.ui.tvshow.TvShowScreen
import com.hkm.flixhub.ui.tvshow.TvShowViewModel
import com.hkm.flixhub.utils.EspressoIdlingResource
import com.hkm.flixhub.utils.RecyclerViewTestUtil
import com.hkm.flixhub.utils.RecyclerViewTestUtil.Companion.atPosition
import org.junit.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class MainActivityTest : KoinTest {
    private lateinit var dataMovie: ArrayList<ShowEntity>
    private lateinit var dataTvShow: ArrayList<ShowEntity>
    private var dataMovieDetail: DetailShowEntity? = null
    private var dataTvShowDetail: DetailShowEntity? = null
    private var movieId: String? = null
    private var tvShowId: String? = null

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
                        check(RecyclerViewTestUtil.hasSize(dataMovie.size))
                        this@onScreen.progressBar.isGone()

                        for ((index, movie) in dataMovie.withIndex()) {
                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index, hasDescendant(withText(movie.title)))))
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

                onView(withText(getResourceString(R.string.movie))).perform(ViewActions.click())
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
                iconShare {
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
                        check(RecyclerViewTestUtil.hasSize(dataTvShow.size))
                        this@onScreen.progressBar.isGone()

                        for ((index, tvShow) in dataTvShow.withIndex()) {
                            perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                                index))
                            check(matches(atPosition(index, hasDescendant(withText(tvShow.title)))))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun loadDetailShow() {
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

                onView(withText(getResourceString(R.string.tv_show))).perform(ViewActions.click())
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
                iconShare {
                    click()
                    intended(hasAction(Intent.ACTION_CHOOSER))
                    intended(hasExtra(Intent.EXTRA_TITLE, getResourceString(R.string.share_title)))
                }
            }
        }
    }

    private fun startTest(app: FlixHubInstrumentedTest, block: () -> Unit) {
        app.loadModules(module {
            single { RemoteDataSource(get()) }
            single<ShowRepository> { ShowRepositoryImpl(get()) }
            single<FakeShowRepository> { FakeShowRepositoryImpl(get()) }

            viewModel { MovieViewModel(get()) }
            viewModel { TvShowViewModel(get()) }
            viewModel { DetailViewModel(get()) }
        }) {
            val mFakeShowRepository: FakeShowRepository by inject()

            dataMovie = mFakeShowRepository.getAllMovies()
            dataTvShow = mFakeShowRepository.getAllTvShows()

            while (true) {
                try {
                    movieId = dataMovie[0].showId
                    tvShowId = dataTvShow[0].showId
                    if (!movieId.isNullOrEmpty() && !tvShowId.isNullOrEmpty())
                        break
                } catch (e: Exception) {
                    idle(250)
                }
            }

            val movieDetails = mFakeShowRepository.getMovieDetail(movieId.toString())
            val tvShowDetails = mFakeShowRepository.getTvShowDetail(tvShowId.toString())
            while (true) {
                try {
                    dataMovieDetail = movieDetails[0]
                    dataTvShowDetail = tvShowDetails[0]
                    if (dataMovieDetail != null && dataTvShowDetail != null)
                        break
                } catch (e: Exception) {
                    idle(250)
                }
            }

            block()
        }
    }
}