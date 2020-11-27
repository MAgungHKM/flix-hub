package com.hkm.flixhub

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner


class KoinTestRunner : AndroidJUnitRunner() {
    @Throws(Exception::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, FlixHubInstrumentedTest::class.java.name, context)
    }
}