package com.hkm.flixhub.ui.favorite.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE
import com.hkm.flixhub.utils.PaginationUtils.MAX_PAGE
import com.hkm.flixhub.utils.SortUtils.DEFAULT

class FavoriteMovieViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var movies: LiveData<PagedList<ShowEntity>>? = null
    private var pages = MutableLiveData<String>()
    private var sortBy = MutableLiveData<String>()

    init {
        pages.value = FIRST_PAGE
        sortBy.value = DEFAULT
    }

    fun getPages(): LiveData<String> = pages

    fun getSortBy(): LiveData<String> = sortBy

    fun nextPage() {
        val page = (pages.value?.toInt() as Int) + 1
        if (page <= MAX_PAGE)
            pages.value = page.toString()
    }

    fun getMovies(
        sort: String = DEFAULT,
        page: String = FIRST_PAGE,
    ): LiveData<PagedList<ShowEntity>> {
        if (movies == null || movies?.value.isNullOrEmpty() ||
            (movies?.value as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE)
        )
            loadMovies(sort, page)

        return movies as LiveData<PagedList<ShowEntity>>
    }

    private fun loadMovies(sort: String, page: String) {
        movies = showRepository.getFavoritedMovies(sort, page)
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
        pages.value = FIRST_PAGE
        val emptyList = MutableLiveData<PagedList<ShowEntity>>()
        movies = emptyList
    }

    fun setSortBy(sort: String) {
        sortBy.value = sort
    }

    override fun onCleared() {
        super.onCleared()
        pages.value = FIRST_PAGE
        sortBy.value = DEFAULT
    }
}