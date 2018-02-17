package com.isanechek.wallpaper.view.main.fragments.details

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val detailsModule = applicationContext {
    viewModel { DetailsViewModel(get()) }
}