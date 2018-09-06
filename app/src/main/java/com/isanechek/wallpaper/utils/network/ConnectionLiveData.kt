package com.isanechek.wallpaper.utils.network

import androidx.lifecycle.LiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI

class ConnectionLiveData(
        private val ctx: Context
) : LiveData<Connection>() {

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        ctx.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        ctx.unregisterReceiver(networkReceiver)
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.extras != null) {
                val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
                if (isConnected) when (activeNetwork.type) {
                    TYPE_WIFI -> postValue(Connection(Connection.WIFI))
                    TYPE_MOBILE -> postValue(Connection(Connection.MOBILE))
                    else -> postValue(Connection(Connection.OFFLINE))
                }
            }
        }
    }
}