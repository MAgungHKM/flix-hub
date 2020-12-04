package com.hkm.flixhub.ui.favorite.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.PaginationUtils
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE

class FavoriteMovieViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var movies: LiveData<PagedList<ShowEntity>>? = null
    private var pages = MutableLiveData<String>()

    init {
        pages.value = FIRST_PAGE
    }

    fun getPages(): LiveData<String> = pages

    fun nextPage() {
        val page = (pages.value?.toInt() as Int) + 1
        if (page <= PaginationUtils.MAX_PAGE)
            pages.postValue(page.toString())
    }

    fun getMovies(page: String): LiveData<PagedList<ShowEntity>> {
        if (movies == null || movies?.value.isNullOrEmpty() ||
            (movies?.value as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE)
        )
            loadMovies(page)

        return movies as LiveData<PagedList<ShowEntity>>
    }

    private fun loadMovies(page: String) {
        movies = showRepository.getFavoritedMovies(page)
    }

    fun removeAllFavorite() {
        if (movies != null) {
            val movieList = movies?.value
            if (movieList != null) {
                for (movie in movieList) {
                    val newState = !movie.favorited
                    showRepository.setShowFavorite(movie, newState)
                }
            }
        }
        val emptyList = MutableLiveData<PagedList<ShowEntity>>()
        movies = emptyList
    }

    override fun onCleared() {
        super.onCleared()
        pages.value = "0"
    }
}