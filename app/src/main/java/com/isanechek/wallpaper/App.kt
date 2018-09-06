package com.isanechek.wallpaper

import android.app.Application
import com.isanechek.repository.di.repositoryModule
import com.isanechek.storage.di.storageModule
import com.isanechek.wallpaper.di.diModule
import com.isanechek.wallpaper.update.updateModule
import com.isanechek.wallpaper.di.utilsModule
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.android.startKoin

/**
 * Created by isanechek on 7/11/17.
 */

private const val API_KEY = "b152aafd-b684-4772-ae86-33f2e2e86cec"
//const val DEFAULT_ALBUM_KEY = "https://yadi.sk/d/J9TUmUT33ZSzGD"  // for debug
const val DEFAULT_ALBUM_KEY = "https://yadi.sk/d/Gc62sdjD3ZesTn"

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(
                diModule,
                utilsModule,
                updateModule,
                storageModule,
                repositoryModule))

        val config = YandexMetricaConfig
                .newConfigBuilder(API_KEY)
                .withSessionTimeout(60)
                .build()
        YandexMetrica.activate(this, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}