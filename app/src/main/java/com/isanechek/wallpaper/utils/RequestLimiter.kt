package com.isanechek.wallpaper.utils

import android.os.SystemClock
import com.isanechek.common.DebugUtils
import com.isanechek.wallpaper.common.PrefManager
import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
class RequestLimiter<in KEY>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = androidx.collection.ArrayMap<KEY, Long>()
    private val timeout: Long = timeUnit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(key: KEY): Boolean {
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > timeout) {
            timestamps[key] = now
            return true
        }
        return false
    }

    @Synchronized
    fun shouldFetch(pref: PrefManager, debug: DebugUtils): Boolean {
        val lastFetched = pref.updateTime
        val now = now()

        debug.log("lastFetched $lastFetched")

        if (now - lastFetched > timeout) {
            pref.updateTime = now
            return true
        }

        debug.log("lastFetched fal")
        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) {
        if (timestamps.containsKey(key)) {
            timestamps.remove(key)
        }
    }
}
