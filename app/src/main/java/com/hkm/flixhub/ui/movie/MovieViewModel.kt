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
import com.hkm.flixhub.vo.Resource

class MovieViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var movies: LiveData<Resource<PagedList<ShowEntity>>>? = null
    private var pages = MutableLiveData<String>()

    init {
        pages.value = FIRST_PAGE
    }

    fun getPages(): LiveData<String> = pages

    fun nextPage() {
        val page = (pages.value?.toInt() as Int) + 1
        if (page <= MAX_PAGE)
            pages.postValue(page.toString())
    }

    fun getMovies(page: String): LiveData<Resource<PagedList<ShowEntity>>> {
        if (movies == null || movies?.value?.data.isNullOrEmpty() ||
            (movies?.value?.data as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE)
        )
            loadMovies(page)

        return movies as LiveData<Resource<PagedList<ShowEntity>>>
    }

    private fun loadMovies(page: String) {
        movies = showRepository.getAllMovies(page)
    }

    override fun onCleared() {
        super.onCleared()
        pages.value = "0"
    }
}