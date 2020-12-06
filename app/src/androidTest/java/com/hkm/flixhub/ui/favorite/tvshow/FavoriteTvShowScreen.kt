package com.hkm.flixhub.ui.favorite.tvshow

import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.hkm.flixhub.R
import com.hkm.flixhub.ui.tvshow.TvShowItem

class FavoriteTvShowScreen : Screen<FavoriteTvShowScreen>() {
    val tvFavoriteTvShowNotFound = KTextView { withId(R.id.tv_favorite_tv_show_not_found) }
    val progressBar = KProgressBar { withId(R.id.progress_bar_favorite_tv_show) }
    val rvFavoriteTvShow = KRecyclerView({
        withId(R.id.rv_favorite_tv_show)
    }, itemTypeBuilder = {
        itemType(::TvShowItem)
    })
}