package com.isanechek.repository.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.isanechek.common.models.Category
import com.isanechek.repository.Repository
import com.isanechek.storage.dao.CategoryDao
import com.isanechek.storage.entity.CategoryEntity
import com.isanechek.common.DebugUtils
import com.isanechek.repository.NetworkState
import com.isanechek.repository.Request
import com.isanechek.wallpaper.data.network.YandexService
import com.isanechek.wallpaper.data.network.dto.Resource
import com.isanechek.repository.Listing
import com.isanechek.extensions.ifError
import com.isanechek.extensions.ifException
import com.isanechek.extensions.ifFailed
import com.isanechek.extensions.ifSucceeded
import com.isanechek.wallpaper.utils.isUiThread
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import ru.gildor.coroutines.retrofit.awaitResult

class CategoryRepository(private val debug: DebugUtils,
                         private val api: YandexService,
                         private val dao: CategoryDao)
    : Repository<Category> {

    private val networkState = MutableLiveData<NetworkState>()

    override fun listing(request: Request?): Listing<Category> {
        val cache = LivePagedListBuilder(dao.load().map {
            debug.log("$TAG item -> ${it.title}")
            Category(
                    id = it.id,
                    lastUpdateDate = it.lastUpdateDate,
                    previewUrl = it.previewUrl,
                    publicKey = it.publicKey,
                    publicPath = it.publicPath,
                    title = it.title
            )
        }, PagedList.Config.Builder()
                .setPageSize(2)
                .build())
                .build()

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }
        return Listing(
                pagedList = cache,
                networkState = networkState,
                retry = {},
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }

    override suspend fun loadData(request: Request?) {
        request ?: return
        debug.log("$TAG load data -->> path ${request.path}")
        postLoadState()
        api.loadCategory2(
                request.key,
                request.path,
                YandexService.SORTING_MODIFIED,
                200,
                0)
                .awaitResult()
                .ifSucceeded {
                    loadCategory(it)
                }
                .ifFailed {
                    postErrorStatus(_string.load_category_failed_msg)
                }.ifError {
                    isErrorCode(it)
                }.ifException {
                    debug.sendStackTrace(it, "Load category exception! Path -->> ${request.path}")
                }
    }

    private suspend fun postLoadState()
            = withContext(CommonPool) {
        val count = dao.getCategoriesSize()
        val state = if (count == 0) NetworkState.INITIAL else NetworkState.LOADING
        networkState.postValue(state)
    }

    private fun refresh(): LiveData<NetworkState> {
        return MutableLiveData<NetworkState>()
    }

    private fun isErrorCode(code: Int) {
        when (code) {
            YandexService.ERROR_CODE_BAD_REQUEST -> postErrorStatus(_string.error_msg_bad_request)
            YandexService.ERROR_CODE_NOT_FIND -> postErrorStatus(_string.error_msg_not_find)
        }
    }

    private fun postErrorStatus(id: Int) {
        networkState.postValue(
                NetworkState.error(
                        msgId = id
                )
        )
    }

    private suspend fun loadCategory(resource: Resource) = withContext(CommonPool) {
        debug.log("$TAG load category")

        val folders = CategoriesMapping.switchMapFolder(resource)
        debug.log("$TAG load category -->> folders size ${folders.size}")

        if (folders.isNotEmpty()) {
            val temp = mutableListOf<CategoryEntity>()
            for (folder in folders) {
                debug.log("$TAG load category -->> category ${folder.name}")
                val response = api.loadCategoryData(
                        key = folder.key,
                        path = folder.path,
                        previewSize = "M",
                        previewCrop = true).execute()
                if (!response.isSuccessful) {
                    val code = response.code()
                    debug.log("$TAG load category ${folder.name} is error ${isErrorCode(code)} code $code")
                    break
                }

                val category = CategoriesMapping.mapToCategory(folder, response.body(), false, debug)
                temp.add(category)
            }

            networkState.postValue(NetworkState.LOADED)
            saveData(temp)
        } else {
            debug.sendStackTrace(null, "Root folder empty!!!")
            postErrorStatus(_string.error_msg_root_folder_empty)
        }
    }

    private fun saveData(categories: List<CategoryEntity>) {
        debug.log("$TAG save data -->> run thread is main $isUiThread")
        val count = dao.getCategoriesSize()
        debug.log("$TAG save data -->> count cache $count")
        if (count > 0) {
            val tempIds = categories.map { it.id }.toSet()
            val cacheIds = dao.loadCategoriesIds().map { it.id }.toSet()

            val removeIds = cacheIds.filterNot { it in tempIds }.toSet()
            val tempUpdate = dao.loadCategories().filter { it.id in tempIds }.toList()
            val insertTemp = categories.filterNot { it.id in cacheIds }.toList()
            dao.transaction(removeIds, tempUpdate, insertTemp)
            debug.log("$TAG save data -->> remove count ${removeIds.size}")
            debug.log("$TAG save data -->> update count ${tempUpdate.size}")
            debug.log("$TAG save data -->> insert count ${insertTemp.size}")
        } else {
            dao.insert(categories)
        }
    }

}

private const val TAG = "CategoryRepository"