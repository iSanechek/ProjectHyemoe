package com.isanechek.wallpaper.di

import android.preference.PreferenceManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

/**
 * Created by isanechek on 4/11/18.
 */
val prefModule = applicationContext {
    provide { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
}