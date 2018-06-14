package com.isanechek.wallpaper

import android.app.Application
import com.isanechek.wallpaper.di.diModule
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.android.startKoin

/**
 * Created by isanechek on 7/11/17.
 */

private const val API_KEY = "b152aafd-b684-4772-ae86-33f2e2e86cec"

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(diModule))

        val config = YandexMetricaConfig
                .newConfigBuilder(API_KEY)
                .withSessionTimeout(60)
                .build()
        YandexMetrica.activate(this, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}