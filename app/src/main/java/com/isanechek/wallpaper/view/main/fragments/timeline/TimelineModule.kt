package com.isanechek.wallpaper.view.main.fragments.timeline

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val timelineModule = applicationContext {
    viewModel {
        TimelineViewModel(get(), get())
    }
}