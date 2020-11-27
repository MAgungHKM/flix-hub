package com.hkm.flixhub.ui.tvshow

import android.view.View
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.hkm.flixhub.R
import org.hamcrest.Matcher

class TvShowScreen : Screen<TvShowScreen>() {
    val progressBar = KProgressBar { withId(R.id.progress_bar_tv_show) }
    val rvTvShow = KRecyclerView({
        withId(R.id.rv_tv_show)
    }, itemTypeBuilder = {
        itemType(::TvShowItem)
    })
}

class TvShowItem(parent: Matcher<View>) : KRecyclerItem<TvShowItem>(parent) {
    val imgTvShow = KImageView {
        withId(R.id.img_tv_show)
        withMatcher(parent)
    }
    val tvShowName = KTextView {
        withId(R.id.tv_show_name)
        withMatcher(parent)
    }
}