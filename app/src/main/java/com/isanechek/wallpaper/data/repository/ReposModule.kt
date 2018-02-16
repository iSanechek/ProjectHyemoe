package com.isanechek.wallpaper.data.repository

import org.koin.dsl.module.applicationContext

val reposModule = applicationContext {
    provide { YaRepository(get(), get()) } bind Repository::class
}