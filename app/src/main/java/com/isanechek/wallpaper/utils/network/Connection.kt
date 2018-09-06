package com.isanechek.wallpaper.utils.network

data class Connection(val type: Int) {
    companion object {
        const val MOBILE = 2
        const val WIFI = 1
        const val OFFLINE = 0
    }
}