package com.isanechek.wallpaper

import android.app.Application
import com.isanechek.wallpaper.di.diModule
import org.koin.android.ext.android.startKoin

/**
 * Created by isanechek on 7/11/17.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(diModule))
    }
}