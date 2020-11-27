package com.hkm.flixhub.di

import com.hkm.flixhub.data.source.ShowRepository
import com.hkm.flixhub.data.source.ShowRepositoryImpl
import com.hkm.flixhub.data.source.remote.RemoteDataSource
import com.hkm.flixhub.ui.detail.DetailViewModel
import com.hkm.flixhub.ui.movie.MovieViewModel
import com.hkm.flixhub.ui.tvshow.TvShowViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RemoteDataSource(androidContext()) }
    single<ShowRepository> { ShowRepositoryImpl(get()) }

    viewModel { MovieViewModel(get()) }
    viewModel { TvShowViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}