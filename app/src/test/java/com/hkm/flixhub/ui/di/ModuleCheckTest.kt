package com.hkm.flixhub.ui.di

import androidx.test.core.app.ApplicationProvider
import com.hkm.flixhub.FlixHubTest
import com.hkm.flixhub.di.appModule
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.stopKoin
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 21, maxSdk = 28, application = FlixHubTest::class)
@Category(CheckModuleTest::class)
class ModuleCheckTest {

    private val app: FlixHubTest = ApplicationProvider.getApplicationContext()

    @get:Rule
    val mockProviderException = MockProviderRule.create {
        mockk(it::class.java.name)
    }

    @After
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun checkAppModules() = checkModules {
        androidContext(app)
        modules(appModule)
    }
}