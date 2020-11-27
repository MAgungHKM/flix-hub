package com.hkm.flixhub.ui.home

import com.agoda.kakao.pager.KViewPager
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.tabs.KTabLayout
import com.hkm.flixhub.R

class HomeScreen : Screen<HomeScreen>() {
    val tabs = KTabLayout { withId(R.id.tabs) }
    val viewPager = KViewPager { withId(R.id.view_pager) }
}