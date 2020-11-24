package com.hkm.flixhub.ui.detail

import android.app.Application
import com.hkm.flixhub.utils.DataDummy
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 21, maxSdk = 28, manifest = Config.NONE, application = Application::class)
class DetailViewModelTest {
    private lateinit var viewModel: DetailViewModel
    private val dummyMovie = DataDummy.generateDummyMovies()[0]
    private val dummyTvShow = DataDummy.generateDummyTvShows()[0]
    private val movieId = dummyMovie.showId
    private val tvShowId = dummyTvShow.showId

    @Before
    fun setUp() {
        viewModel = DetailViewModel()
    }

    @Test
    fun getMovies() {
        viewModel.setSelectedShow(movieId)
        viewModel.setSelectedShowType(DetailFragment.TYPE_MOVIE)
        val showEntity = viewModel.getShow()
        assertNotNull(showEntity)
        assertEquals(dummyMovie.showId, showEntity.showId)
        assertEquals(dummyMovie.releaseDate, showEntity.releaseDate)
        assertEquals(dummyMovie.synopsis, showEntity.synopsis)
        assertEquals(dummyMovie.imagePath, showEntity.imagePath)
        assertEquals(dummyMovie.title, showEntity.title)
        assertEquals(dummyMovie.director, showEntity.director)
        assertEquals(dummyMovie.genre, showEntity.genre)
        assertEquals(dummyMovie.quote, showEntity.quote)
        assertEquals(dummyMovie.score, showEntity.score)
    }

    @Test
    fun getTvShows() {
        viewModel.setSelectedShow(tvShowId)
        viewModel.setSelectedShowType(DetailFragment.TYPE_TV_SHOW)
        val showEntity = viewModel.getShow()
        assertNotNull(showEntity)
        assertEquals(dummyTvShow.showId, showEntity.showId)
        assertEquals(dummyTvShow.releaseDate, showEntity.releaseDate)
        assertEquals(dummyTvShow.synopsis, showEntity.synopsis)
        assertEquals(dummyTvShow.imagePath, showEntity.imagePath)
        assertEquals(dummyTvShow.title, showEntity.title)
        assertEquals(dummyTvShow.director, showEntity.director)
        assertEquals(dummyTvShow.genre, showEntity.genre)
        assertEquals(dummyTvShow.quote, showEntity.quote)
        assertEquals(dummyTvShow.score, showEntity.score)
    }

    @Test
    fun throwUninitializedPropertyAccessExceptionByType() {
        viewModel.setSelectedShow(movieId)
        viewModel.setSelectedShowType("Unknown")
        assertThrows(UninitializedPropertyAccessException::class.java) { viewModel.getShow() }
    }

    @Test
    fun throwUninitializedPropertyAccessExceptionById() {
        viewModel.setSelectedShow("-1")
        viewModel.setSelectedShowType(DetailFragment.TYPE_MOVIE)
        assertThrows(UninitializedPropertyAccessException::class.java) { viewModel.getShow() }
    }
}