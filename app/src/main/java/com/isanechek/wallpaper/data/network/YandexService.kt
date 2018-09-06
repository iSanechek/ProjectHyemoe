package com.isanechek.wallpaper.data.network

import com.isanechek.wallpaper.data.network.dto.Link
import com.isanechek.wallpaper.data.network.dto.Resource
import com.isanechek.wallpaper.utils.exception.ServerIOException
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

interface YandexService {

    @GET(RESOURCE_V1)
    @Throws(IOException::class, ServerIOException::class)
    fun loadCategory(@Query("public_key") publicKey: String,
                     @Query("path") path: String,
                     @Query("limit") limit: Int,
                     @Query("offset") offset: Int): Call<Resource>

    @GET(RESOURCE_V1)
    @Throws(IOException::class, ServerIOException::class)
    fun loadCategory2(@Query("public_key") publicKey: String,
                     @Query("path") path: String,
                      @Query("sort") sort: String,
                     @Query("limit") limit: Int,
                     @Query("offset") offset: Int): Call<Resource>

    @GET(RESOURCE_V1)
    @Throws(IOException::class, ServerIOException::class)
    fun loadCategoryData(@Query("public_key") key: String,
                         @Query("path") path: String,
                         @Query("preview_size") previewSize: String,
                         @Query("preview_crop") previewCrop: Boolean): Call<Resource>

    @GET(RESOURCE_V1)
    @Throws(IOException::class, ServerIOException::class)
    fun loadWallpapers(@Query("public_key") publicKey: String,
                       @Query("path") path: String,
                       @Query("limit") limit: Int,
                       @Query("offset") offset: Int,
                       @Query("preview_size") previewSize: String,
                       @Query("preview_crop") previewCrop: Boolean): Call<Resource>

    @GET(DOWNLOAD_V1)
    @Throws(IOException::class, ServerIOException::class)
    fun loadDownloadLink(@Query("public_key") publicKey: String,
                        @Query("path") path: String): Call<Link>

    @GET(RESOURCE_V1)
    @Throws(IOException::class, ServerIOException::class)
    fun loadRoot(@Query("public_key") publicKey: String): Call<Resource>

    companion object {
        private const val RESOURCE_V1 = "/v1/disk/public/resources"
        private const val DOWNLOAD_V1 = "/v1/disk/public/resources/download"
        val defaultHeader: Pair<String, String> = Pair("Accept", "application/json")

        // request sorting
        const val SORTING_MODIFIED = "modified"

        // error code
        const val ERROR_CODE_BAD_REQUEST = 400
        const val ERROR_CODE_NOT_FIND = 404

    }
}