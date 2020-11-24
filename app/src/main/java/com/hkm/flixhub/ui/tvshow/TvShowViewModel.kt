package com.hkm.flixhub.ui.tvshow

import androidx.lifecycle.ViewModel
import com.hkm.flixhub.entity.ShowEntity
import com.hkm.flixhub.utils.DataDummy

class TvShowViewModel : ViewModel() {

    fun getTvShows(): ArrayList<ShowEntity> = DataDummy.generateDummyTvShows()
}