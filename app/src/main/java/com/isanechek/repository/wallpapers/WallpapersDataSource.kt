package com.isanechek.repository.wallpapers

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.isanechek.common.models.Wallpaper
import com.isanechek.common.DebugUtils
import com.isanechek.repository.NetworkState
import com.isanechek.repository.Request
import com.isanechek.wallpaper.data.network.YandexService

class WallpapersDataSource(private val debug: DebugUtils,
                           private val api: YandexService,
                           private val request: Request) : PositionalDataSource<Wallpaper>() {

    val networkState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Wallpaper>) {
        networkState.postValue(NetworkState.INITIAL)
        val result = api.loadWallpapers(
                publicKey = request.key,
                path = request.path,
                limit = params.requestedLoadSize,
                offset = 0,
                previewSize = "M",
                previewCrop = false)
                .execute()
        if (result.isSuccessful) {
            val body = result.body()!!
            val name = body.name
            val resource = body.resourceList
            val total = resource.total
            val offset = resource.total
            debug.log("$TAG loadInitial ->> name $name")
            debug.log("$TAG loadInitial ->> total size $total")
            debug.log("$TAG loadInitial ->> offset size $offset")
            debug.log("$TAG loadInitial ->> resource size ${resource.items.size}")
            val items = WallpaperMapping.map(resource = resource.items, category = name)
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(items, 0, total)
        } else {
            debug.log("Initial Load Error Message ${result.message()} Code ${result.code()}")
            networkState.postValue(NetworkState.error(msg = "Load error ${result.message()}"))
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Wallpaper>) {
        networkState.postValue(NetworkState.LOADING)
        val result = api.loadWallpapers(
                publicKey = request.key,
                path = request.path,
                previewSize = "M",
                limit = params.loadSize,
                offset = params.startPosition,
                previewCrop = true)
                .execute()

        if (result.isSuccessful) {
            val body = result.body()!!
            val resource = body.resourceList
            debug.log("$TAG loadRange ->> name ${body.name}")
            debug.log("$TAG loadRange ->> resource size ${resource.items.size}")
            val items = WallpaperMapping.map(resource = resource.items, category = body.name)
            callback.onResult(items)
        }
    }
}

private const val TAG = "WallpapersDataSource"