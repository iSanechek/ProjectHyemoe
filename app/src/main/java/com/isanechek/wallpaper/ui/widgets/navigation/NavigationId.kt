package com.isanechek.wallpaper.ui.widgets.navigation

import com.isanechek.extensions.emptyString
import com.isanechek.wallpaper.ui.about.AboutFragment
import com.isanechek.wallpaper.ui.dialogs.NoWallpaperDialog
import com.isanechek.wallpaper.ui.category.CategoryFragment
import com.isanechek.wallpaper.ui.timeline.TimelineFragment

sealed class NavigationId(val name: String = emptyString, val fullName: String = emptyString) {

    object TIMELINE : NavigationId("Timeline", TimelineFragment::class.java.name)
    object CATEGORY : NavigationId("Category", CategoryFragment::class.java.name)
    object NOWALLPAPER: NavigationId("No wallpaper", NoWallpaperDialog::class.java.name)
    object ABOUT : NavigationId("APP INFO", AboutFragment::class.java.name)
}