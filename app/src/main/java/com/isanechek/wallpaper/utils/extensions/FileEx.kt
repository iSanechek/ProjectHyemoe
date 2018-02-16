package com.isanechek.wallpaper.utils.extensions

import java.io.File

/**
 * Created by isanechek on 11/26/17.
 */

fun fileIsExists(strFile: String): Boolean {
    try {
        val f = File(strFile)
        if (!f.exists()) {
            return false
        }

    } catch (e: Exception) {
        return false
    }
    return true
}

fun fileIsExists(f: File): Boolean {
    try {
        if (!f.exists()) {
            return false
        }

    } catch (e: Exception) {
        return false
    }

    return true
}


