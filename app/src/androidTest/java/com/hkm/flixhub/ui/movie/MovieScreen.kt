package com.hkm.flixhub.ui.movie

import android.view.View
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.hkm.flixhub.R
import org.hamcrest.Matcher

class MovieScreen : Screen<MovieScreen>() {
    val progressBar = KProgressBar { withId(R.id.progress_bar_movie) }
    val rvMovie = KRecyclerView({
        withId(R.id.rv_movie)
    }, itemTypeBuilder = {
        itemType(::MovieItem)
    })
}

class MovieItem(parent: Matcher<View>) : KRecyclerItem<MovieItem>(parent) {
    val imgMovie = KImageView {
        withId(R.id.img_movie)
        withMatcher(parent)
    }
    val tvMovieName = KTextView {
        withId(R.id.tv_movie_name)
        withMatcher(parent)
    }
}