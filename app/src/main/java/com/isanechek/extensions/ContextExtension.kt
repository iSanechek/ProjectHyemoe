package com.isanechek.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import androidx.core.content.getSystemService

private val uiHandler = Handler(Looper.getMainLooper())

fun Context.runOnUIThread(runnable: Runnable, delay: Long) {
    if (delay == 0L) {
        uiHandler.post(runnable)
    } else {
        uiHandler.postDelayed(runnable, delay)
    }
}

fun Context.networkConnected(): Boolean {
    val cm = getSystemService<ConnectivityManager>()
    val network: NetworkInfo
    try {
        network = cm!!.activeNetworkInfo
    } catch (t: Throwable) {
        return false
    }
    return network != null && network.isConnected
}

fun Context.inWifi(): Boolean {
    val cm = getSystemService<ConnectivityManager>()
    val network: NetworkInfo
    try {
        network = cm!!.activeNetworkInfo
    } catch (t: Throwable) {
        return false
    }
    return network != null && network.type == ConnectivityManager.TYPE_WIFI
}