package com.hkm.flixhub.ui.favorite.tvshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.utils.PaginationUtils
import com.hkm.flixhub.utils.PaginationUtils.FIRST_PAGE
import com.hkm.flixhub.utils.PaginationUtils.ITEM_PER_PAGE

class FavoriteTvShowViewModel(private val showRepository: ShowRepository) : ViewModel() {
    private var tvShows: LiveData<PagedList<ShowEntity>>? = null
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

    fun getTvShows(page: String): LiveData<PagedList<ShowEntity>> {
        if (tvShows == null || tvShows?.value.isNullOrEmpty() ||
            (tvShows?.value as PagedList).size < ((getPages().value?.toInt() as Int) * ITEM_PER_PAGE)
        )
            loadTvShows(page)

        return tvShows as LiveData<PagedList<ShowEntity>>
    }

    private fun loadTvShows(page: String) {
        tvShows = showRepository.getFavoritedTvShows(page)
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
    }

    override fun onCleared() {
        super.onCleared()
        pages.value = "0"
    }
}