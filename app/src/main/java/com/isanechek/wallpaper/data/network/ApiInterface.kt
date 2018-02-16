package com.isanechek.wallpaper.data.network

import com.isanechek.wallpaper.data.network.dto.Link
import com.isanechek.wallpaper.data.network.dto.Resource
import com.isanechek.wallpaper.utils.exception.ServerIOException
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

interface ApiInterface {

    @GET("/v1/disk/public/resources")
    @Throws(IOException::class, ServerIOException::class)
    fun getPublicList(@Query("public_key") publicKey: String): Deferred<Resource>

    @GET("/v1/disk/public/resources")
    @Throws(IOException::class, ServerIOException::class)
    fun getImgUrls(@Query("public_key") publicKey: String, @Query("path") path: String,
                @Query("limit") limit: Int?, @Query("preview_size") previewSize: String,
                @Query("preview_crop") previewCrop: Boolean): Deferred<Resource>

    @GET("/v1/disk/public/resources/download")
    @Throws(IOException::class, ServerIOException::class)
    fun getDownloadLink(@Query("public_key") publicKey: String, @Query("path") path: String): Deferred<Link>

}