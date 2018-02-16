package com.isanechek.wallpaper.view.widgets.navigation

interface NavItemClickListener {
    operator fun invoke(item: NavigationItem, position: Int)
}