package com.isanechek.network

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.response

class Api {

    fun getAlbums(publicKey: String, callback: ApiCallback<Any>) {
        callback.loading(null)
        request(YandexApi.AlbumAndCoverAlbum(publicKey))
                .response(AlbumsAndCover.Deserializer(), { request, response, result ->
                    result.fold(success = { item ->
                        val info = ResponseMapping.mappingResponseInfo(item)
                        val albums = ResponseMapping.mappingAlbums(item)
                        callback.success(Pair(info, albums))
                    }, failure = { error ->
                        callback.error(error.message)
                    })
                })
    }

    private fun request(api: Fuel.RequestConvertible): Request =
            Fuel.request(api).timeout(20000)

    interface ApiCallback<T> {
        fun loading(data: T?)
        fun success(data: T)
        fun error(message: String?)
    }
}