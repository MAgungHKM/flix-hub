package com.hkm.flixhub.ui.tvshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE
import com.hkm.flixhub.utils.PaginationUtils.MAX_PAGE
import com.hkm.flixhub.utils.SortUtils
import com.hkm.flixhub.vo.Resource

class TvShowViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var tvShows: LiveData<Resource<PagedList<ShowEntity>>>? = null
    private var pages = MutableLiveData<String>()
    private var sortBy = MutableLiveData<String>()

    init {
        pages.value = FIRST_PAGE
        sortBy.value = SortUtils.POPULARITY
    }

    fun getPages(): LiveData<String> = pages

    fun getSortBy(): LiveData<String> = sortBy

    fun nextPage() {
        val page = (pages.value?.toInt() as Int) + 1
        if (page <= MAX_PAGE)
            pages.postValue(page.toString())
    }

    fun getTvShows(sort: String, page: String): LiveData<Resource<PagedList<ShowEntity>>> {
        if (tvShows == null || tvShows?.value?.data.isNullOrEmpty() || (tvShows?.value?.data as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE))
            loadTvShows(sort, page)

        return tvShows as LiveData<Resource<PagedList<ShowEntity>>>
    }

    private fun loadTvShows(sort: String, page: String) {
        tvShows = showRepository.getAllTvShows(sort, page)
    }

    fun refreshTvShows() {
        pages.value = FIRST_PAGE
        val emptyList = MutableLiveData<Resource<PagedList<ShowEntity>>>()
        tvShows = emptyList
    }

    fun setSortBy(sort: String) {
        sortBy.value = sort
    }

    override fun onCleared() {
        super.onCleared()
        pages.value = FIRST_PAGE
        sortBy.value = SortUtils.POPULARITY
    }
}