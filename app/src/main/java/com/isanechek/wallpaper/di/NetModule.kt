package com.isanechek.wallpaper.di

import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.data.network.ApiInterface
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.applicationContext
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.kotlin.createRetrofitService
import java.util.concurrent.TimeUnit

val netModule = applicationContext {
    bean {
        createOkHttpClient()
    }

    bean {
        createYandexApi(get())
    }
}

fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
}

fun createYandexApi(client: OkHttpClient): ApiInterface {
    return createRetrofitService {
        this.baseUrl = "https://cloud-api.yandex.net"
        this.client = client
        converterFactories = arrayListOf(GsonConverterFactory.create())
        callAdapterFactories = arrayListOf(CoroutineCallAdapterFactory())
    }
}