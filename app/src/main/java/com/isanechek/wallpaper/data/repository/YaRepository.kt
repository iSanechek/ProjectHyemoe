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
import com.isanechek.wallpaper.utils.Const
import com.isanechek.wallpaper.utils.FileUtils
import com.isanechek.wallpaper.utils.RequestLimiter
import com.isanechek.wallpaper.utils.logger
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.util.concurrent.TimeUnit

class YaRepository(private val context: Context,
                   private val api: ApiInterface,
                   private val database: DataBase,
                   preferences: SharedPreferences) : Repository {
    private val requestLimiter = RequestLimiter<String>(1, TimeUnit.DAYS, preferences) // Нехер больше. В край просто передернуть надо
    private val statusMessage: MutableLiveData<Response<Any>> by lazy { MutableLiveData<Response<Any>>() }
    val status: LiveData<Response<Any>> = statusMessage

    override suspend fun loadCategory(strategy: RequestStrategy) {
        when (strategy) {
            is RequestStrategy.EMPTY_REQUEST -> {
                logger("$TAG empty request")
                statusMessage.value = Response.loading(message = Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS)
                loadDataCategory()
            }
            is RequestStrategy.DATA_REQUEST -> if (requestLimiter.shouldFetch("category")) {
                logger("${TAG} request limiter")
                statusMessage.value = Response.loading(message = Const.REQUEST_UPDATE_AND_SHOW_SMALL_PROGRESS)
                loadDataCategory()
            } else {
                statusMessage.value = Response.success(message = Const.WAIT_AND_SHOW_NO_NEW)
                logger("${TAG} Соси писю!!!")
            }
            is RequestStrategy.UPDATE_REQUEST -> {
                logger("${TAG} update category")
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
            logger("$TAG loadCategory Cache >>Not<< Empty size: ${cache.size}")

            // Check items for remove
            logger("$TAG loadCategory Check Data for remove")
            val newData = result.map { it.title }.toSet()
            cache.filterNot { it.title in newData }
                    .map { item ->
                        logger("$TAG Category item for remove ${item.title}")
                        async(CommonPool) {
                            database.wallpaper().removeCategory(item)
                        }.await()
                    }

            // Check items for update
            logger("$TAG loadCategory Check Data for update")
            result.filter { n -> cache.any { o -> o.title == n.title && o.lastUpdate != n.lastUpdate} }
                    .map { item ->
                        logger("$TAG Category item for update ${item.title}")
                        async(CommonPool) {
                            database.wallpaper().updateCategory(item)
                        }.await()
                    }

            // Check new items
            logger("$TAG loadCategory Check >>NEW<< Data")
            val oldData = cache.map { it.title }.toSet()
            result.filterNot { it.title in oldData }
                    .map { item ->
                        logger("$TAG Category new item ${item.title}")
                        async(CommonPool) {
                            database.wallpaper().insertCategory(item)
                        }.await()
                    }

        } else {
            logger("$TAG loadCategory Cache Empty -> Insert All size: ${cache.size}")
            async(CommonPool) {
                database.wallpaper().insertAllCategory(result)
            }.await()
        }
    }

    override suspend fun loadImages(category: String, strategy: RequestStrategy) {
        when (strategy) {
            is RequestStrategy.EMPTY_REQUEST -> {
                logger("$TAG loadImages empty request")
                statusMessage.value = Response.loading(message = Const.REQUEST_DATA_AND_SHOW_BIG_PROGRESS)
                loadDataWallpapers(category)
            }
            is RequestStrategy.DATA_REQUEST -> if (requestLimiter.shouldFetch(category)) {
                logger("$TAG loadImages request limiter $category")
                statusMessage.value = Response.loading(message = Const.REQUEST_UPDATE_AND_SHOW_SMALL_PROGRESS)
                loadDataWallpapers(category)
            } else {
                statusMessage.value = Response.success(message = Const.WAIT_AND_SHOW_NO_NEW)
                logger("$TAG Соси писю!!!")
            }
            is RequestStrategy.UPDATE_REQUEST -> {
                logger("$TAG loadImages update category $category")
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
                    true )
                    .await()

            val result = MappingData.mappingResourceToWallpapers2(response, category)
            logger("$TAG loadDataWallpapers result ${result.size}")
            val cache = async(CommonPool) {
                database.wallpaper().loadAllWallpapersCategory(category)
            }.await()
            logger("$TAG loadDataWallpapers size: ${cache.size}")

            // Check items for remove
            val newData = result.map { it.title }.toSet()
            cache.filterNot { it.title in newData }.map { item ->
                logger("$TAG Wallpaper item for remove ${item.title}")
                database.wallpaper().removeWallpaper(item)
            }

            // Check items for update
            result.filter { n -> cache.any { o -> o.title == n.title && o.lastUpdate != n.lastUpdate } }
                    .map { item ->
                        logger("$TAG Wallpaper item for update ${item.title}")
                        async(CommonPool) {
                            database.wallpaper().updateWallpaper(item)
                        }.await()
                    }

            // Check new items
            val oldData = cache.map { it.title }.toSet()
            result.filterNot { it.title in oldData }
                    .map { item ->
                        logger("$TAG Wallpaper new item ${item.title}")
                        async(CommonPool) {
                            database.wallpaper().insertWallpaper(item)
                        }.await()
                    }

        } else {
            // бля, такого не должно произойти, но раз на раз и пидорас. так что перестрахуемся.))
            // Вообще, это когда нибудь надо переписать.
            logger("$TAG loadDataWallpapers Boom")
            statusMessage.postValue(Response.error(message = Const.BAD_REQUEST))
        }
    }

    val _data = MutableLiveData<Wallpaper>()
    override suspend fun loadImage(item: Wallpaper) {
        val key = item.publicKey!!
        val ppath = item.publicPath!!
        logger("Key $key Path $ppath")
        val response = api.getDownloadLink(item.publicKey!!, item.publicPath!!).await()
        val path = FileUtils.saveFile(context, response.href)
        logger("result path $path")
        item.fullCachePath = path
//        async {
//            database.wallpaper().updateWallpaper(item)
//        }.await()

        _data.postValue(item)
    }


    override suspend fun updateWallpaper(wallpaper: Wallpaper) {
        async {
            database.wallpaper().updateWallpaper(wallpaper)
        }
    }

    override fun loadWallpaper(id: String): LiveData<Wallpaper> = _data
//            database.wallpaper().loadWallpaperLiveData(id)

    companion object {
        private const val TAG = "YaRepository"
//        private const val DEFAULT_KEY = "https://yadi.sk/d/OPeqGfrb3TnF5y"
        private const val DEFAULT_KEY = "https://yadi.sk/d/x8WJ-PcL3JNkwy"
    }
}