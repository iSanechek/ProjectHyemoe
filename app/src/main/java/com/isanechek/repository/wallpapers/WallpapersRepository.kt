package com.isanechek.repository.wallpapers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.isanechek.common.models.Wallpaper
import com.isanechek.extensions.switchMap
import com.isanechek.repository.Repository
import com.isanechek.common.DebugUtils
import com.isanechek.repository.NetworkState
import com.isanechek.repository.Request
import com.isanechek.wallpaper.data.network.YandexService
import com.isanechek.repository.Listing
import java.util.concurrent.Executors

class WallpapersRepository(private val debug: DebugUtils,
                           private val api: YandexService) : Repository<Wallpaper> {

    private val config = PagedList.Config.Builder()
            .setPageSize(30)
            .setPrefetchDistance(10)
            .setInitialLoadSizeHint(30)
            .build()

    private fun refresh(): LiveData<NetworkState> {
        return MutableLiveData<NetworkState>()
    }

    override fun listing(request: Request?): Listing<Wallpaper> {
        val factory = DataSourceFactory(debug, api, request!!)
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }
        return Listing(
                pagedList = LivePagedListBuilder(factory, config)
                        .setFetchExecutor(Executors.newSingleThreadExecutor())
                        .build(),
                networkState = factory
                        .dataSource
                        .switchMap {
                            it.networkState
                        },
                refreshState = refreshState,
                refresh = {
                    refreshTrigger.value = null
                },
                retry = {}
        )
    }

    override suspend fun loadData(request: Request?) {}

}