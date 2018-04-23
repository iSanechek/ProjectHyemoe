package com.isanechek.network

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.ResponseDeserializable

/**
 * Created by isanechek on 4/13/18.
 */

data class ResponseInfo(val revision: Long,
                        val modified: String,
                        val name: String,
                        val limit: Int,
                        val offset: Int,
                        val total: Int)

data class Album(val created: String,
                 val modified: String,
                 val name: String,
                 val path: String,
                 val key: String,
                 val url: String,
                 val type: String,
                 val revision: Int,
                 val resourceId: String,
                 var fileUrl: String? = emptyString,
                 var md5: String? = emptyString,
                 var mediaType: String? = emptyString,
                 var mimeType: String? = emptyString,
                 var previewUrl: String? = emptyString,
                 var size: Int? = 0)

data class AlbumsAndCover(@Json(name = "public_url") val key: String,
                      @Json(name = "revision") val revision: Long,
                      @Json(name = "modified") val modified: String,
                      @Json(name = "name") val name: String,
                      @Json(name = "_embedded") val embedded: Embedded) {
    class Deserializer : ResponseDeserializable<AlbumsAndCover> {
        override fun deserialize(content: String): AlbumsAndCover? = Klaxon().parse(content)
    }
}
data class Embedded(@Json(name = "limit") val limit: Int,
                    @Json(name = "offset") val offset: Int,
                    @Json(name = "total") val total: Int,
                    @Json(name = "items") val items: ArrayList<JsonObject>)