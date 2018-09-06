package com.isanechek.wallpaper.ui.navigation

sealed class BackStrategy {
    object KEEP : BackStrategy()
    object DESTROY : BackStrategy()
}