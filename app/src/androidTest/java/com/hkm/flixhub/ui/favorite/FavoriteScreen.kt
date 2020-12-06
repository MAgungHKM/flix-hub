package com.hkm.flixhub.ui.favorite

import com.agoda.kakao.pager.KViewPager
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.tabs.KTabLayout
import com.hkm.flixhub.R

class FavoriteScreen : Screen<FavoriteScreen>() {
    val tabsFavorite = KTabLayout { withId(R.id.tabs_favorite) }
    val viewPagerFavorite = KViewPager { withId(R.id.view_pager_favorite) }
}