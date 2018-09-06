package com.isanechek.repository.wallpapers

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.isanechek.common.models.Wallpaper
import com.isanechek.common.DebugUtils
import com.isanechek.repository.Request
import com.isanechek.wallpaper.data.network.YandexService

class DataSourceFactory(private val debug: DebugUtils,
                        private val api: YandexService,
                        private val request: Request) : DataSource.Factory<Int, Wallpaper>() {

    val dataSource = MutableLiveData<WallpapersDataSource>()

    override fun create(): DataSource<Int, Wallpaper> = WallpapersDataSource(debug, api, request)
            .also {
                dataSource.postValue(it)
            }

}