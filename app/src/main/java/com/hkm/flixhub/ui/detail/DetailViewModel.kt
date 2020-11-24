package com.hkm.flixhub.ui.detail

import androidx.lifecycle.ViewModel
import com.hkm.flixhub.entity.ShowEntity
import com.hkm.flixhub.utils.DataDummy

class DetailViewModel : ViewModel() {
    private lateinit var showId: String
    private lateinit var showType: String

    fun setSelectedShow(showId: String) {
        this.showId = showId
    }

    fun setSelectedShowType(showType: String) {
        this.showType = showType
    }

    fun getShow(): ShowEntity {
        lateinit var show: ShowEntity
        var showsEntities = ArrayList<ShowEntity>()
        when (showType) {
            DetailFragment.TYPE_MOVIE -> showsEntities = DataDummy.generateDummyMovies()
            DetailFragment.TYPE_TV_SHOW -> showsEntities = DataDummy.generateDummyTvShows()
        }

        for (showEntity in showsEntities) {
            if (showEntity.showId == showId) {
                show = showEntity
            }
        }
        return show
    }
}