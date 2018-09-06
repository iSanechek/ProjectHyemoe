package com.isanechek.wallpaper.di

import android.content.Context
import android.content.SharedPreferences
import com.isanechek.repository.di.CATEGORY_REPO
import com.isanechek.repository.di.WALLPAPERS_REPO
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.common.PrefManager
import com.isanechek.wallpaper.data.network.YandexService
import com.isanechek.wallpaper.data.pref.PrefManagerImpl
import com.isanechek.wallpaper.utils.NotificationUtil
import com.isanechek.wallpaper.utils.network.ConnectionLiveData
import com.isanechek.wallpaper.ui.details.DetailScreenViewModel
import com.isanechek.wallpaper.ui.main.MainViewModel
import com.isanechek.wallpaper.ui.category.CategoryViewModel
import com.isanechek.wallpaper.ui.timeline.TimelineViewModel
import com.isanechek.wallpaper.ui.splash.SplashViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by isanechek on 4/23/18.
 */
val diModule = module {
    // main viewmodel
    viewModel {
        MainViewModel(get())
    }

    // timeline viewmodel
    viewModel {
        TimelineViewModel(get(name = WALLPAPERS_REPO))
    }

    // category viewmodel
    viewModel {
        CategoryViewModel(get(name = CATEGORY_REPO), get())
    }

    // detail viewmodel
    viewModel {
        DetailScreenViewModel(
                androidContext(),
                get(),
                get())
    }

    viewModel {
        SplashViewModel(
                get(),
                get())
    }

    single {
        NotificationUtil(get())
    }



    single {
        PrefManagerImpl(get()) as PrefManager
    }


    // network
    single {
        createOkHttpClient()
    }

    single {
        createWebservice(get())
    }

    // preference
    single {
        androidContext()
                .applicationContext
                .getSharedPreferences("wallpapersx", Context.MODE_PRIVATE)
    } bind (SharedPreferences::class)

    single {
        ConnectionLiveData(androidContext())
    }
}

// network
fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
    else HttpLoggingInterceptor.Level.NONE
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
            chain.proceed(request)
        }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
}

fun createWebservice(client: OkHttpClient): YandexService {
    return Retrofit.Builder()
            .baseUrl("https://cloud-api.yandex.net")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YandexService::class.java)
}