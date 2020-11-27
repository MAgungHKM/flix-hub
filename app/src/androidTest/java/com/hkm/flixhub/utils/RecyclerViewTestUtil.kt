package com.hkm.flixhub.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

import androidx.test.espresso.NoMatchingViewException

import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.Is.`is`


class RecyclerViewTestUtil private constructor(private val matcher: Matcher<Int>) :
    ViewAssertion {
    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        assertThat(adapter!!.itemCount, matcher)
    }

    companion object {
        fun hasSize(expectedCount: Int): RecyclerViewTestUtil {
            return hasSize(`is`(expectedCount))
        }

        private fun hasSize(matcher: Matcher<Int>): RecyclerViewTestUtil {
            return RecyclerViewTestUtil(matcher)
        }

        fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("Has item at position $position: ")
                    itemMatcher.describeTo(description)
                }

                override fun matchesSafely(view: RecyclerView): Boolean {
                    val mViewHolder =
                        view.findViewHolderForAdapterPosition(position) ?: return false
                    return itemMatcher.matches(mViewHolder.itemView)
                }

            }
        }
    }

}