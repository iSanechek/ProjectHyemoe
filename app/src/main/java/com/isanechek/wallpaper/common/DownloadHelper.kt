package com.isanechek.wallpaper.common

interface DownloadHelper {
    fun downloadBitmap(url: String): String
    fun downloadApk(url: String): String
}