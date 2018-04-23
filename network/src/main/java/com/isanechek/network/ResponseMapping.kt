package com.isanechek.network

import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonReader
import junit.framework.Assert
import java.io.StringReader

const val CREATED_KEY = "created"
const val MODIFIED_KEY = "modified"
const val NAME_KEY = "name"
const val PATH_KEY = "path"
const val PUBLIC_KEY_KEY = "public_key"
const val RESOURCE_ID_KEY = "resource_id"
const val REVISION_KEY = "revision"
const val TYPE_KEY = "type"
const val FILE_URL_KEY = "file"
const val MD5_KEY = "md5"
const val MEDIA_TYPE_KEY = "media_type"
const val MIME_TYPE_KEY = "mime_type"
const val PREVIEW_URL_KEY = "preview"
const val SIZE_KEY = "size"

object ResponseMapping {

    fun mappingResponseInfo(item: AlbumsAndCover): ResponseInfo =
            ResponseInfo(revision = item.revision,
                    modified = item.modified,
                    name = item.name,
                    limit = item.embedded.limit,
                    offset = item.embedded.offset,
                    total = item.embedded.total)

    fun mappingAlbums(item: AlbumsAndCover): List<Album> {
        val items = parsingAlbums(item.embedded.items)
        val cavers = items
                .filter {
                    it.mediaType == "image"
                }.toSet()

        return items.filter { it.type == "dir" }
                .map {
                    val cover = cavers.find { cover -> cover.name.replaceFirst(".jpg", "").trim() == it.name }
                    Album(created = it.created,
                            modified = it.modified,
                            name = it.name,
                            path = it.path,
                            key = it.key,
                            url = it.url,
                            type = it.type,
                            revision = it.revision,
                            resourceId = it.resourceId,
                            fileUrl = cover?.fileUrl,
                            md5 = cover?.md5,
                            mediaType = cover?.mediaType,
                            mimeType = cover?.mimeType,
                            previewUrl = cover?.previewUrl,
                            size = cover?.size)
                }
    }

    private fun parsingAlbums(items: ArrayList<JsonObject>): List<Album> = items
            .map {
                parseItem(it)
            }.toList()

    private fun parseItem(item: JsonObject): Album {
        var key = emptyString
        var created = emptyString
        var modified = emptyString
        var name = emptyString
        var path = emptyString
        var resId = emptyString
        var revision = 0
        var type = emptyString

        // if file
        var url = emptyString
        var md5 = emptyString
        var mediaType = emptyString
        var mimeType = emptyString
        var preview = emptyString
        var size = 0

        Log.e("TEST", "JO $item")

//        JsonReader(StringReader(item)).use {reader ->
//            reader.beginObject {
//                while (reader.hasNext()) {
//                    val readName = reader.nextName()
//                    when (readName) {
//                        CREATED_KEY -> created = reader.nextString()
//                        MODIFIED_KEY -> modified = reader.nextString()
//                        NAME_KEY -> name = reader.nextString()
//                        PATH_KEY -> path = reader.nextString()
//                        PUBLIC_KEY_KEY -> key = reader.nextString()
//                        RESOURCE_ID_KEY -> resId = reader.nextString()
//                        REVISION_KEY -> revision = reader.nextInt()
//                        TYPE_KEY -> type = reader.nextString()
//                        else -> Assert.fail("Boom name $readName")
//                    }
//
//                    if (type == "file") {
//                        when (readName) {
//                            FILE_URL_KEY -> url = reader.nextString()
//                            MD5_KEY -> md5 = reader.nextString()
//                            MEDIA_TYPE_KEY -> mediaType = reader.nextString()
//                            MIME_TYPE_KEY -> mimeType = reader.nextString()
//                            PREVIEW_URL_KEY -> preview = reader.nextString()
//                            SIZE_KEY -> size = reader.nextInt()
//                            else -> Assert.fail("Boom name2 $readName")
//                        }
//                    }
//                }
//            }
//        }

        return Album(
                created = created,
                modified = modified,
                name = name,
                path = path,
                key = key,
                resourceId = resId,
                revision = revision,
                type = type,
                url = url,
                fileUrl = url,
                md5 = md5,
                mediaType = mediaType,
                mimeType = mimeType,
                previewUrl = preview,
                size = size)
    }
}