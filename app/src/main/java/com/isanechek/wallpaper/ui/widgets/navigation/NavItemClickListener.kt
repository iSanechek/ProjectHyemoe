package com.isanechek.wallpaper.ui.widgets.navigation

interface NavItemClickListener {
    operator fun invoke(item: NavigationItem, position: Int)
}