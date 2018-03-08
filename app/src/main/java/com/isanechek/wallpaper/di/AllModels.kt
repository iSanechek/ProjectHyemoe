package com.isanechek.wallpaper.di

import com.isanechek.wallpaper.data.database.databaseModule
import com.isanechek.wallpaper.data.repository.reposModule
import com.isanechek.wallpaper.utils.notificationUtilsModule
import com.isanechek.wallpaper.view.main.fragments.category.categoryModule
import com.isanechek.wallpaper.view.main.fragments.details.detailsModule
import com.isanechek.wallpaper.view.main.fragments.timeline.timelineModule
import com.isanechek.wallpaper.view.main.mainModel

val allModels = listOf(
        netModule,
        databaseModule,
        reposModule,
        categoryModule,
        detailsModule,
        timelineModule,
        mainModel,
        notificationUtilsModule
)