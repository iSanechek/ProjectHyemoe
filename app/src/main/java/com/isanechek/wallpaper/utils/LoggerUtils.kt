package com.isanechek.wallpaper.utils

import android.util.Log
import com.isanechek.wallpaper.BuildConfig

/**
 * Created by isanechek on 9/4/17.
 */
fun logger(msg: String?) {
    if (msg != null) {
        if (!BuildConfig.DEBUG) return
        Log.e(LoggerUtils.TAG, msg)
        return
    }
    Log.e(LoggerUtils.TAG, "Log message null")
}

inline fun <reified T> T.withLog(): T {
    logger("${T::class.java.simpleName} $this")
    return this
}

object LoggerUtils {
    const val TAG = "wtf"


    // later
//    private var debugMode: Boolean = false
//    fun init(debug: Boolean) {
//        this.debugMode = debug
//    }


}