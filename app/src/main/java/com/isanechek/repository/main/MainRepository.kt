package com.isanechek.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isanechek.common.DebugUtils
import com.isanechek.wallpaper.common.PrefManager
import com.isanechek.wallpaper.models.Info
import com.isanechek.repository.NetworkState
import com.isanechek.wallpaper.data.network.YandexService
import com.isanechek.wallpaper.data.network.dto.Resource
import com.isanechek.wallpaper.models.Update
import com.isanechek.extensions.await
import com.isanechek.extensions.emptyString
import com.isanechek.extensions.ifFailed
import com.isanechek.extensions.ifSucceeded
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.gildor.coroutines.retrofit.awaitResult

interface MainRepository {
    suspend fun loadData()
    fun resource(): LiveData<Pair<Info, Resource>>
    fun network(): MutableLiveData<NetworkState>
}

class MainRepositoryImpl(private val debug: DebugUtils,
                         private val webservice: YandexService,
                         private val pref: PrefManager,
                         private val ok: OkHttpClient) : MainRepository {

    private val _resource = MutableLiveData<Pair<Info, Resource>>()
    private val _networkState = MutableLiveData<NetworkState>()

    override fun network(): MutableLiveData<NetworkState> = _networkState
    override fun resource(): LiveData<Pair<Info, Resource>> = _resource

    override suspend fun loadData() {
        _networkState.postValue(NetworkState.LOADING)
        debug.log("$TAG loadData -->> ${pref.defaultAlbumKey}")

        webservice.loadRoot(pref.defaultAlbumKey)
                .awaitResult()
                .ifSucceeded {
                    val (update, resource) = parseInfo(it)
                    _resource.postValue(Pair(update.data!!.second, resource))
                    debug.log("$TAG loadData -->> Succeeded")
                    debug.log("$TAG loadData -->> Update ${update.data.second.removeAlbums}")
                }
                .ifFailed {
                    debug.log("$TAG loadData -->> Failed")
//                    repo.networkState(NetworkState.error("Failed load data!"))
                }

    }

    private suspend fun parseInfo(resource: Resource): Pair<JsonResponse, Resource> {
        debug.log("$TAG >>parseInfo<<")
        val jsonUrl = resource
                .resourceList
                .items
                .first {
                    it.type.equals("file", ignoreCase = true)
                }.file
        debug.log("$TAG parseInfo -->> jsonUrl $jsonUrl")
        loadJson(jsonUrl)

        val root = resource
                .resourceList
                .items
                .first {
                    it.type.equals("dir", ignoreCase = true)
                }
        debug.log("$TAG parseInfo -->> Root $root")

        return Pair(first = loadJson(jsonUrl), second = root)

    }

    private suspend fun loadJson(jsonUrl: String): JsonResponse =
            withContext(CommonPool) {
                debug.log("$TAG loadJson -->> json url $jsonUrl")
                if (jsonUrl.isEmpty()) JsonResponse(data = null, errorMessage = "Json url is empty!")
                val request = Request.Builder().url(jsonUrl).build()
                val result = ok.newCall(request).await()
                val res = if (result.isSuccessful) result.body()?.string() ?: emptyString else emptyString
                if(res.isNotEmpty())JsonResponse(source = res)
                else JsonResponse(data = null, errorMessage = "Response is empty!")
    }
}

data class JsonResponse(val data: Pair<Update, Info>?, val errorMessage: String?) {

    constructor(source: String): this (JsonResponse.mapping(source), null)

    companion object {
        fun mapping(string: String): Pair<Update, Info> {
            val root = JSONObject(string)
            val update = root.getJSONObject("update_info")
            val info = root.getJSONObject("system_info")
            return Pair(
                    first = Update(
                            version = update.optInt("version_code"),
                            url = update.optString("version_url"),
                            notes = update.optString("version_note"),
                            messag = update.optString("version_message")),
                    second = Info(
                            alternativeUrl = info.optString("alternative_url"),
                            removeAlbums = info.optString("remove_albums")))
        }
    }
}
private const val TAG = "MainRepository"