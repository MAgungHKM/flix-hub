package com.hkm.flixhub.ui.favorite.movie

import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.hkm.flixhub.R
import com.hkm.flixhub.ui.movie.MovieItem

class FavoriteMovieScreen : Screen<FavoriteMovieScreen>() {
    val tvFavoriteMovieNotFound = KTextView { withId(R.id.tv_favorite_movie_not_found) }
    val progressBar = KProgressBar { withId(R.id.progress_bar_favorite_movie) }
    val rvFavoriteMovie = KRecyclerView({
        withId(R.id.rv_favorite_movie)
    }, itemTypeBuilder = {
        itemType(::MovieItem)
    })
}