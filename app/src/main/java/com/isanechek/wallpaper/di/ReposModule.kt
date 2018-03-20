package com.isanechek.wallpaper.di

import com.isanechek.wallpaper.data.repository.Repository
import com.isanechek.wallpaper.data.repository.YaRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val reposModule = applicationContext {
    provide { YaRepository(androidApplication(), get(), get()) } bind Repository::class
}