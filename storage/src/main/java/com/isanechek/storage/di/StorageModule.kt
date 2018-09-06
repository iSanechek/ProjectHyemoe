package com.isanechek.storage.di

import androidx.room.Room
import com.isanechek.storage.WallpapersDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val storageModule = module {
    single {
        Room.databaseBuilder(
                androidContext(),
                WallpapersDatabase::class.java,
                "WallpapersX.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    single {
        get<WallpapersDatabase>().categoryDao()
    }

    single {
        get<WallpapersDatabase>().wallpaperDao()
    }
}