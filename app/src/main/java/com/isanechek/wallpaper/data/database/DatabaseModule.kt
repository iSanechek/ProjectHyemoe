package com.isanechek.wallpaper.data.database

import android.arch.persistence.room.Room
import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val databaseModule = applicationContext {
    provide { providerDatabase(androidApplication()) }
}

fun providerDatabase(appContext: Context): DataBase {
    return Room.databaseBuilder(appContext, DataBase::class.java, "Wallpapers.db")
            .fallbackToDestructiveMigration()
            .build()
}
