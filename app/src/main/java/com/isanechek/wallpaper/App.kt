package com.isanechek.wallpaper

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.isanechek.wallpaper.di.allModels
import org.koin.android.ext.android.startKoin

/**
 * Created by isanechek on 7/11/17.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, allModels)
        instance = this
    }

    fun getPreferences() : SharedPreferences = applicationContext.getSharedPreferences("default", Context.MODE_PRIVATE)

    companion object{ lateinit var instance: App private set }

}