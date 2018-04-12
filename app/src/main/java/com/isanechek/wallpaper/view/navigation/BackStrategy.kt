package com.isanechek.wallpaper.view.navigation

sealed class BackStrategy {
    object KEEP : BackStrategy()
    object DESTROY : BackStrategy()
}