package com.hkm.flixhub.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE
import com.hkm.flixhub.utils.PaginationUtils.MAX_PAGE
import com.hkm.flixhub.utils.SortUtils.POPULARITY
import com.hkm.flixhub.vo.Resource

class MovieViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var movies: LiveData<Resource<PagedList<ShowEntity>>>? = null
    private var pages = MutableLiveData<String>()
    private var sortBy = MutableLiveData<String>()

    init {
        pages.value = FIRST_PAGE
        sortBy.value = POPULARITY
    }

    fun getPages(): LiveData<String> = pages

    fun getSortBy(): LiveData<String> = sortBy

    fun nextPage() {
        val page = (pages.value?.toInt() as Int) + 1
        if (page <= MAX_PAGE)
            pages.value = page.toString()
    }

    fun getMovies(
        sort: String = POPULARITY,
        page: String = FIRST_PAGE,
    ): LiveData<Resource<PagedList<ShowEntity>>> {
        if (movies == null || movies?.value?.data.isNullOrEmpty() || (movies?.value?.data as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE))
            loadMovies(sort, page)

        return movies as LiveData<Resource<PagedList<ShowEntity>>>
    }

    private fun loadMovies(sort: String, page: String) {
        movies = showRepository.getAllMovies(sort, page)
    }

    fun refreshMovies() {
        pages.value = FIRST_PAGE
        val emptyList = MutableLiveData<Resource<PagedList<ShowEntity>>>()
        movies = emptyList
    }

    fun setSortBy(sort: String) {
        sortBy.value = sort
    }

    override fun onCleared() {
        super.onCleared()
        pages.value = FIRST_PAGE
        sortBy.value = POPULARITY
    }
}