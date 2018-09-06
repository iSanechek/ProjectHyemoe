package com.isanechek.wallpaper.di

import com.isanechek.common.AppExecutors
import com.isanechek.common.DebugUtils
import com.isanechek.wallpaper.common.DownloadHelper
import com.isanechek.wallpaper.utils.impl.AppExecutorsImpl
import com.isanechek.wallpaper.utils.impl.DebugUtilsImpl
import com.isanechek.wallpaper.utils.impl.DownloadHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val utilsModule = module {
    single {
        DebugUtilsImpl() as DebugUtils
    }

    single {
        DownloadHelperImpl(
                androidContext(),
                get(),
                get()) as DownloadHelper
    }



    single {
        AppExecutorsImpl() as AppExecutors
    }
}