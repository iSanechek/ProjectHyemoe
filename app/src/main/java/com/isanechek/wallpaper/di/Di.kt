package com.isanechek.wallpaper.di

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.data.database.DataBase
import com.isanechek.wallpaper.data.network.ApiInterface
import com.isanechek.wallpaper.data.repository.Repository
import com.isanechek.wallpaper.data.repository.YaRepository
import com.isanechek.wallpaper.utils.NotificationUtil
import com.isanechek.wallpaper.utils.network.ConnectionLiveData
import com.isanechek.wallpaper.view.details.DetailScreen
import com.isanechek.wallpaper.view.details.DetailScreenViewModel
import com.isanechek.wallpaper.view.main.MainViewModel
import com.isanechek.wallpaper.view.main.fragments.category.CategoryViewModel
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.kotlin.createRetrofitService
import java.util.concurrent.TimeUnit

/**
 * Created by isanechek on 4/23/18.
 */
val diModule = applicationContext {
    // main viewmodel
    viewModel {
        MainViewModel(get())
    }

    // timeline viewmodel
    viewModel {
        TimelineViewModel(get(), get())
    }

    // category viewmodel
    viewModel {
        CategoryViewModel(get(), get())
    }

    // detail viewmodel
    viewModel {
        DetailScreenViewModel(get())
    }

    bean {
        NotificationUtil(get())
    }

    provide {
        YaRepository(androidApplication(), get(), get(), get())
    } bind Repository::class

    // database
    provide {
        providerDatabase(androidApplication())
    }

    // network
    bean {
        createOkHttpClient()
    }

    bean {
        createYandexApi(get())
    }

    // preference
    provide {
        sharedPreferences(get())
    }

    bean {
        ConnectionLiveData(androidApplication())
    }
}


// database
fun providerDatabase(appContext: Context): DataBase {
    return Room.databaseBuilder(appContext, DataBase::class.java, "Wallpapers.db")
        .fallbackToDestructiveMigration()
        .build()
}

// network
fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
    else HttpLoggingInterceptor.Level.NONE
    return OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}

fun createYandexApi(client: OkHttpClient): ApiInterface = createRetrofitService {
    this.baseUrl = "https://cloud-api.yandex.net"
    this.client = client
    converterFactories = arrayListOf(GsonConverterFactory.create())
    callAdapterFactories = arrayListOf(CoroutineCallAdapterFactory())
}

fun sharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}