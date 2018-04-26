package com.isanechek.wallpaper

import android.app.Application
import com.isanechek.wallpaper.di.diModule
import com.yandex.metrica.YandexMetrica
import org.koin.android.ext.android.startKoin

/**
 * Created by isanechek on 7/11/17.
 */

private const val API_KEY = "43614695-4bad-431c-9e14-fa588179b756" // replace

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(diModule))

        YandexMetrica.activate(applicationContext, API_KEY)
    }
}