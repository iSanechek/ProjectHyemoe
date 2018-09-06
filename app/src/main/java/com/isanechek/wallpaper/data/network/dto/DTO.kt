package com.isanechek.wallpaper.data.network.dto

import com.google.gson.annotations.SerializedName
import com.isanechek.extensions.emptyString
import com.isanechek.wallpaper.utils.parse
import java.util.*

data class Link(
        @SerializedName("href") var href: String = emptyString,
        @SerializedName("method") var method: String = emptyString,
        @SerializedName("templated") var isTemplated: Boolean = false)

data class ResourceList(
        @SerializedName("sort") var sort: String = emptyString,
        @SerializedName("public_key") var publicKey: String = emptyString,
        @SerializedName("items") var items: List<Resource> = emptyList(),
        @SerializedName("path") var path: String = emptyString,
        @SerializedName("limit") var limit: Int = 0,
        @SerializedName("offset") var offset: Int = 0,
        @SerializedName("total") var total: Int = 0)

data class Resource(
        @SerializedName("public_key") var publicKey: String = emptyString,
        @SerializedName("resource_id") var id: String = emptyString,
        @SerializedName("_embedded") var resourceList: ResourceList = ResourceList(),
        @SerializedName("name") var name: String = emptyString,
        @SerializedName("created") var created: String = emptyString,
        @SerializedName("public_url") var publicUrl: String = emptyString,
        @SerializedName("origin_path") var originPath: String = emptyString,
        @SerializedName("modified") var modified: String = emptyString,
        @SerializedName("deleted") var deleted: String = emptyString,
        @SerializedName("path") var path: String = emptyString,
        @SerializedName("file") var file: String = emptyString,
        @SerializedName("md5") var md5: String = emptyString,
        @SerializedName("type") var type: String = emptyString,
        @SerializedName("mime_type") var mimeType: String = emptyString,
        @SerializedName("media_type") var mediaType: String = emptyString,
        @SerializedName("preview") var preview: String = emptyString,
        @SerializedName("size") var size: Long = 0L,
        @SerializedName("custom_properties") var properties: Any = emptyString) {
    fun createDate(date: String?): Date? = if (date != null) parse(date) else null
    fun getModified(modified: String?): Date? = if (modified != null) parse(modified) else null
    fun getDeleted(deleted: String?): Date? = if (deleted != null) parse(deleted) else null
    fun getOriginPath(path: String?): ResourcePath? = if (path != null) ResourcePath(path) else null
    fun getPath(path: String?): ResourcePath? = if (path != null) ResourcePath(path) else null
}

data class ResourcePath(val str: String?) {
    var prefix: String = emptyString
    var path: String = emptyString

    companion object {
        private const val SEPARATOR = ':'
    }

    init {
        if (str == null) {
            throw IllegalArgumentException()
        }
        val index = str.indexOf(ResourcePath.SEPARATOR)
        if (index == -1) {
            prefix = emptyString
            path = str
        } else {
            prefix = str.substring(0, index)
            path = str.substring(index + 1)
            if (prefix.isEmpty()) {
                throw IllegalArgumentException()
            }
        }
        if (path.isEmpty()) {
            throw IllegalArgumentException()
        }
    }
}