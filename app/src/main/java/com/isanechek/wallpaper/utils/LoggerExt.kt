package com.isanechek.wallpaper.utils

import android.util.Log
import java.util.logging.Logger

/**
 * Created by isanechek on 9/4/17.
 */
fun logger(msg: String?) {
    if (msg == null) {
        Log.e("Wallpaper", "Log message null")
        return
    }

    Log.e("Wallpaper", msg)
}

inline fun log(message: () -> Any?) {
//    Logger.log(message())
}

inline fun <reified T> T.withLog(): T {
    log("${T::class.java.simpleName} $this")
    return this
}

fun log(vararg message: () -> Any?) {
    message.forEach {
        log(it())
    }
}

fun log(message: Any?) {
//    Logger.log(message)
}