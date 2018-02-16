package com.isanechek.wallpaper.view.main.fragments.category

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val categoryModule = applicationContext {
    viewModel {
        CategoryViewModel(get(), get())
    }
}