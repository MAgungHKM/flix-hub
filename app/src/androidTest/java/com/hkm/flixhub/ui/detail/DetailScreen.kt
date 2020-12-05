package com.hkm.flixhub.ui.detail

import com.agoda.kakao.image.KImageView
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.hkm.flixhub.R

class DetailScreen : Screen<DetailScreen>() {
    val progressBar = KProgressBar { withId(R.id.progress_bar_detail) }
    val imgBanner = KImageView { withId(R.id.img_banner) }
    val imgPoster = KImageView { withId(R.id.img_poster) }
    val btnShare = KImageView { withId(R.id.btn_share) }
    val btnFavorite = KImageView { withId(R.id.btn_favorite) }
    val tvQuote = KTextView { withId(R.id.tv_quote) }
    val tvScore = KTextView { withId(R.id.tv_score) }
    val tvTitle = KTextView { withId(R.id.tv_title) }
    val tvGenre = KTextView { withId(R.id.tv_genre) }
    val tvDirector = KTextView { withId(R.id.tv_director) }
    val tvDate = KTextView { withId(R.id.tv_date) }
    val tvSynopsis = KTextView { withId(R.id.tv_synopsis) }
}