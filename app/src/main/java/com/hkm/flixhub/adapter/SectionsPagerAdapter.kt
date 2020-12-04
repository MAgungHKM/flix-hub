package com.hkm.flixhub.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hkm.flixhub.R
import com.hkm.flixhub.ui.favorite.FavoriteFragment
import com.hkm.flixhub.ui.favorite.movie.FavoriteMovieFragment
import com.hkm.flixhub.ui.favorite.tvshow.FavoriteTvShowFragment
import com.hkm.flixhub.ui.home.HomeFragment
import com.hkm.flixhub.ui.movie.MovieFragment
import com.hkm.flixhub.ui.tvshow.TvShowFragment

class SectionsPagerAdapter(
    private val mTag: String,
    private val mContext: Context,
    fm: FragmentManager,
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.movie, R.string.tv_show)
    }

    override fun getItem(position: Int): Fragment =
        when (mTag) {
            HomeFragment::class.java.simpleName -> {
                when (position) {
                    0 -> MovieFragment()
                    1 -> TvShowFragment()
                    else -> Fragment()
                }
            }
            FavoriteFragment::class.java.simpleName -> {
                when (position) {
                    0 -> FavoriteMovieFragment()
                    1 -> FavoriteTvShowFragment()
                    else -> Fragment()
                }
            }
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence =
        mContext.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = TAB_TITLES.size

}