package com.isanechek.wallpaper.view.navigation

import com.isanechek.wallpaper.utils.Experimental

@Experimental
sealed class BackStrategy {
    object KEEP : BackStrategy()
    object DESTROY : BackStrategy()
}