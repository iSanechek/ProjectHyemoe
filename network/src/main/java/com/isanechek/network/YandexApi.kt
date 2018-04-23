package com.isanechek.network

import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.util.FuelRouting

internal sealed class YandexApi : FuelRouting {

    class AlbumAndCoverAlbum(val publicKey: String) : YandexApi()

    override val basePath: String = "https://cloud-api.yandex.net"

    override val headers: Map<String, String>? = null

    override val method: Method
        get() {
            when (this) {
                is AlbumAndCoverAlbum -> return Method.GET
            }
        }

    override val params: List<Pair<String, Any?>>?
        get() {
            when (this) {
                is AlbumAndCoverAlbum -> return listOf("public_key" to publicKey)
            }
        }

    override val path: String
        get() {
            when (this) {
                is AlbumAndCoverAlbum -> return "/v1/disk/public/resources"
            }
        }
}