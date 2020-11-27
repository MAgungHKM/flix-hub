package com.hkm.flixhub.utils

import androidx.test.espresso.idling.CountingIdlingResource

class EspressoIdlingResource {
    companion object {
        private const val RESOURCE = "GLOBAL"
        private val espressoIdlingResource = CountingIdlingResource(RESOURCE)

        fun increment() {
            espressoIdlingResource.increment()
        }

        fun decrement() {
            espressoIdlingResource.decrement()
        }

        fun getIdlingResource(): CountingIdlingResource = espressoIdlingResource
    }
}