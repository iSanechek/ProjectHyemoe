package com.isanechek.wallpaper.utils.impl

import android.util.Log
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.common.DebugUtils

class DebugUtilsImpl : DebugUtils {

    private val isDebug = BuildConfig.DEBUG

    override fun log(items: List<Any>, message: String) {
        if (isDebug) {
            for (i in items) {
                Log.d(TAG_NAME, "item $message $i")
            }
        }
    }

    override fun sendStackTrace(exception: Throwable?, message: String) {
        if (isDebug) {
            Log.e(TAG_NAME, message, exception)
            exception?.printStackTrace()
        }
    }

    override fun sendStackTrace(exception: Exception?, message: String) {
        if (isDebug) {
            Log.e(TAG_NAME, message, exception)
            exception?.printStackTrace()
        }
    }

    override fun log(message: String?) {
        if (isDebug) {
            Log.d(TAG_NAME, message)
        }
    }

    companion object {
        private const val TAG_NAME = "WallpaperX"
    }

}