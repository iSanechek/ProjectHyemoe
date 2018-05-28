package com.isanechek.wallpaper.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import com.isanechek.wallpaper.data.database.DataBase
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.ApiInterface
import com.isanechek.wallpaper.data.network.MappingData
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.data.network.Response
import com.isanechek.wallpaper.utils.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.concurrent.TimeUnit

class YaRepository(
    private val context: Context,
    private val api: ApiInterface,
    private val database: DataBase,
    preferences: SharedPreferences
) : Repository {
    private val requestLimiter = RequestLimiter<String>(
        1,
        TimeUnit.HOURS,
        preferences
    ) // Нехер больше. В край просто передернуть надо
    private val statusMessage: MutableLiveData<Response<Any>> by lazy { MutableLiveData<Response<Any>>() }
    val status: LiveData<Response<Any>> = statusMessage

    override suspend fun loadCategory(strategy: RequestStrategy) {
        when (strategy) {
            is RequestStrategy.EMPTY_REQUEST -> {
                statusMessage.value =
                        Response.loading(message = Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS)
                loadDataCategory()
            }
            is RequestStrategy.DATA_REQUEST -> if (requestLimiter.shouldFetch("category")) {
                statusMessage.value =
                        Response.loading(message = Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS)
                loadDataCategory()
            } else {
                statusMessage.value = Response.success(message = Const.WAIT_AND_SHOW_NO_NEW)
            }
            is RequestStrategy.UPDATE_REQUEST -> {
                statusMessage.value = Response.loading(message = Const.REQUEST_UPDATE_DATE)
                loadDataCategory()
                requestLimiter.reset("category")
            }
        }
    }

    private suspend fun loadDataCategory() {
        val response = api.getPublicList(DEFAULT_KEY).await()
        val result = MappingData.mappingResourceToAlbum(response)

        val cache = async(CommonPool) {
            database.wallpaper().loadAllCategory()
        }.await()

        if (cache.isNotEmpty()) {
            statusMessage.postValue(Response.loading(message = STATUS_HIDE_BIG_AND_SHOW_SMALL_PROGRESS))

            // Check items for remove
            val newData = result.map { it.title }.toSet()
            cache.filterNot { it.title in newData }
                .map { item ->
                    async(CommonPool) {
                        database.wallpaper().removeCategory(item)
                    }.await()
                }

            // Check items for update
            result.filter { n -> cache.any { o -> o.title == n.title && o.lastUpdate != n.lastUpdate } }
                .map { item ->
                    async(CommonPool) {
                        database.wallpaper().updateCategory(item)
                    }.await()
                }

            // Check new items
            val oldData = cache.map { it.title }.toSet()
            result.filterNot { it.title in oldData }
                .map { item ->
                    async(CommonPool) {
                        database.wallpaper().insertCategory(item)
                    }.await()
                }

        } else {
            async(CommonPool) {
                database.wallpaper().insertAllCategory(result)
            }.await()
            statusMessage.postValue(Response.success(message = STATUS_HIDE_PROGRESS))
        }
    }

    override suspend fun loadImages(category: String, strategy: RequestStrategy) {
        when (strategy) {
            is RequestStrategy.EMPTY_REQUEST -> {
                statusMessage.value =
                        Response.loading(message = Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS)
                loadDataWallpapers(category)
            }
            is RequestStrategy.DATA_REQUEST -> if (requestLimiter.shouldFetch(category)) {
                statusMessage.value =
                        Response.loading(message = Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS)
                loadDataWallpapers(category)
            } else {
                statusMessage.value = Response.loading(message = STATUS_HIDE_PROGRESS)
            }
            is RequestStrategy.UPDATE_REQUEST -> {
                statusMessage.value = Response.loading(message = Const.REQUEST_UPDATE_DATE)
                loadDataWallpapers(category)
            }
        }
    }

    private suspend fun loadDataWallpapers(category: String) {
        if (category.isNotEmpty()) {
            val categoryItem = async(CommonPool) {
                database.wallpaper().loadCategory(category)
            }.await()
            val response = api.getImgUrls(
                categoryItem.publicKey!!,
                categoryItem.publicPath!!,
                50,
                "L",
                true
            )
                .await()

            val result = MappingData.mappingResourceToWallpapers2(response, category)
            val cache = async(CommonPool) {
                database.wallpaper().loadAllWallpapersCategory(category)
            }.await()

            // Check items for remove
            val newData = result.map { it.title }.toSet()
            cache.filterNot { it.title in newData }.map { item ->
                database.wallpaper().removeWallpaper(item)
            }

            // Check items for update
            result.filter { n -> cache.any { o -> o.title == n.title && o.lastUpdate != n.lastUpdate } }
                .map { item ->
                    async(CommonPool) {
                        database.wallpaper().updateWallpaper(item)
                    }.await()
                }

            // Check new items
            val oldData = cache.map { it.title }.toSet()
            result.filterNot { it.title in oldData }
                .map { item ->
                    async(CommonPool) {
                        database.wallpaper().insertWallpaper(item)
                    }.await()
                }

            statusMessage.postValue(Response.loading(message = STATUS_HIDE_PROGRESS))

        } else {
            // бля, такого не должно произойти, но раз на раз и пидорас. так что перестрахуемся.))
            // Вообще, это когда нибудь надо переписать.
            statusMessage.postValue(Response.error(message = Const.BAD_REQUEST))
        }
    }

    private val _data = MutableLiveData<Wallpaper>()
    override suspend fun loadImage(item: Wallpaper) {
        val response = api.getDownloadLink(item.publicKey!!, item.publicPath!!).await()
        val path = FileUtils.saveFile(context, response.href)
        item.fullCachePath = path
        _data.postValue(item)
    }


    override suspend fun updateWallpaper(wallpaper: Wallpaper) {
        async {
            database.wallpaper().updateWallpaper(wallpaper)
        }
    }

    override fun loadWallpaperState(): LiveData<Wallpaper> = _data

    override fun clearData() {
        _data.postValue(null)
    }

    companion object {
        private const val TAG = "YaRepository"
        //        private const val DEFAULT_KEY = "https://yadi.sk/d/OPeqGfrb3TnF5y"
        private const val DEFAULT_KEY = "https://yadi.sk/d/x8WJ-PcL3JNkwy"
    }
}