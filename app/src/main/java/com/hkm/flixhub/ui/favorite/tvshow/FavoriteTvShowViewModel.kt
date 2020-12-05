package com.hkm.flixhub.ui.favorite.tvshow

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

class FavoriteTvShowViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var tvShows: LiveData<PagedList<ShowEntity>>? = null
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

    fun getTvShows(
        sort: String = DEFAULT,
        page: String = FIRST_PAGE,
    ): LiveData<PagedList<ShowEntity>> {
        if (tvShows == null || tvShows?.value.isNullOrEmpty() ||
            (tvShows?.value as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE)
        )
            loadTvShows(sort, page)

        return tvShows as LiveData<PagedList<ShowEntity>>
    }

    private fun loadTvShows(sort: String, page: String) {
        tvShows = showRepository.getFavoritedTvShows(sort, page)
    }

    fun removeAllFavorite() {
        if (tvShows != null) {
            val tvShowList = tvShows?.value
            if (tvShowList != null) {
                for (tvShow in tvShowList) {
                    val newState = !tvShow.favorited
                    showRepository.setShowFavorite(tvShow, newState)
                }
            }
        }
        pages.value = FIRST_PAGE
        val emptyList = MutableLiveData<PagedList<ShowEntity>>()
        tvShows = emptyList
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