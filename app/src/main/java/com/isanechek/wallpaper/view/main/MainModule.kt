package com.isanechek.wallpaper.view.main

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val mainModel = applicationContext {
    viewModel { MainViewModel() }
}