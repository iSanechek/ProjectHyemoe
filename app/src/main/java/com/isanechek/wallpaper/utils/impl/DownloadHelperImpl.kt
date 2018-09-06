package com.isanechek.wallpaper.utils.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.isanechek.common.DebugUtils
import com.isanechek.wallpaper.common.DownloadHelper
import com.isanechek.wallpaper.utils.FileUtils
import com.isanechek.extensions.copyFromInputStream
import com.isanechek.extensions.emptyString
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class DownloadHelperImpl(private val context: Context,
                         private val debug: DebugUtils,
                         private val client: OkHttpClient) : DownloadHelper {

    override fun downloadApk(url: String): String {
        val directory = context.getExternalFilesDir("wallpapers")
        val updateFile = File(directory, "update.apk")
        if (!directory.exists()) directory.mkdirs()

        val request = Request.Builder()
                .url(url)
                .build()
        try {
            val stream = client.newCall(request).execute().body()!!.byteStream()
            updateFile.copyFromInputStream(stream)

        } catch (e: Exception) {
            debug.sendStackTrace(e, "Какая та хуйня с загрузкой битмапины!")
        }
        return FileUtils.getPath(context, Uri.fromFile(updateFile)) ?: emptyString
    }

    override fun downloadBitmap(url: String): String {
        val directory = context.getExternalFilesDir("wallpapers")
        val cacheFile = File(directory, "temp.jpg")
        if (!directory.exists()) directory.mkdirs()

        val request = Request.Builder()
                .url(url)
                .build()

        try {
            val stream = client.newCall(request).execute().body()?.byteStream()
            val bitmap = BitmapFactory.decodeStream(stream)
            FileOutputStream(cacheFile).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }
        } catch (e: Exception) {
            debug.sendStackTrace(e, "Какая та хуйня с загрузкой битмапины!")
        }

        return FileUtils.getPath(context, Uri.fromFile(cacheFile)) ?: emptyString
    }

}