package com.hkm.flixhub.ui

import com.agoda.kakao.image.KImageView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.toolbar.KToolbar
import com.hkm.flixhub.R

class MainScreen : Screen<MainScreen>() {
    val toolbar = KToolbar { withId(R.id.toolbar) }
    val homeButton = KImageView { withId(android.R.id.home) }
}