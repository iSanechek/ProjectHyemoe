package com.isanechek.wallpaper.utils

import android.os.SystemClock
import android.support.v4.util.ArrayMap
import com.isanechek.wallpaper.App

import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RequestLimiter<KEY>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = ArrayMap<KEY, Long>()
    private val timeout: Long = timeUnit.toMillis(timeout.toLong())

    // need rewrite
    private val pref = App.instance.getPreferences()

    @Synchronized
    fun shouldFetch(key: KEY): Boolean {
//        val lastFetched = timestamps[key]
        val lastFetched = pref.getLong(key as String, 0L)
        val now = now()
        if (lastFetched == 0L) {
//            timestamps.put(key, now)
            setPref(key as String, now)
            return true
        }
        if (now - lastFetched > timeout) {
//            timestamps.put(key, now)
            setPref(key as String, now)
            return true
        }
        return false
    }

    private fun setPref(key: String, value: Long) {
        pref.edit().putLong(key, value).apply()
    }

    private fun now(): Long = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) {
        timestamps.remove(key)
    }
}
