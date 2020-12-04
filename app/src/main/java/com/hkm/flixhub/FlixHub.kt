package com.hkm.flixhub

import android.app.Application
import com.hkm.flixhub.di.databaseModule
import com.hkm.flixhub.di.repositoryModule
import com.hkm.flixhub.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FlixHub : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@FlixHub)
            modules(
                repositoryModule,
                viewModelModule,
                databaseModule
            )
        }
    }
}