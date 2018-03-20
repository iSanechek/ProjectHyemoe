package com.isanechek.wallpaper.di

import com.isanechek.wallpaper.utils.notificationUtilsModule
import com.isanechek.wallpaper.view.main.fragments.category.categoryModule
import com.isanechek.wallpaper.view.main.fragments.timeline.timelineModule
import com.isanechek.wallpaper.view.main.mainModel

val allModels = listOf(
        netModule,
        databaseModule,
        reposModule,
        categoryModule,
        timelineModule,
        mainModel,
        notificationUtilsModule
)