package com.isanechek.wallpaper.data.repository

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val reposModule = applicationContext {
    provide { YaRepository(androidApplication(), get(), get()) } bind Repository::class
}