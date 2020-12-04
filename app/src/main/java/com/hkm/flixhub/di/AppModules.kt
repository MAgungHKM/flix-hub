package com.hkm.flixhub.di

import android.app.Application
import androidx.room.Room
import com.hkm.flixhub.data.ShowRepository
import com.hkm.flixhub.data.ShowRepositoryImpl
import com.hkm.flixhub.data.source.local.LocalDataSource
import com.hkm.flixhub.data.source.local.room.ShowDao
import com.hkm.flixhub.data.source.local.room.ShowDatabase
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.ui.detail.DetailViewModel
import com.hkm.flixhub.ui.favorite.movie.FavoriteMovieViewModel
import com.hkm.flixhub.ui.favorite.tvshow.FavoriteTvShowViewModel
import com.hkm.flixhub.ui.movie.MovieViewModel
import com.hkm.flixhub.ui.tvshow.TvShowViewModel
import com.hkm.flixhub.utils.AppExecutors
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { RemoteDataSource(androidContext()) }
    single { LocalDataSource(get()) }
    single { AppExecutors() }
    single<ShowRepository> { ShowRepositoryImpl(get(), get(), get()) }
}

val viewModelModule = module {
    viewModel { MovieViewModel(get()) }
    viewModel { TvShowViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { FavoriteMovieViewModel(get()) }
    viewModel { FavoriteTvShowViewModel(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): ShowDatabase {
        return Room.databaseBuilder(application, ShowDatabase::class.java, "shows")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideShowDao(database: ShowDatabase): ShowDao {
        return database.showDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideShowDao(get()) }
}