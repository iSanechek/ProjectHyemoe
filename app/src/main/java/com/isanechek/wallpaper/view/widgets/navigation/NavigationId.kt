package com.isanechek.wallpaper.view.widgets.navigation

import com.isanechek.wallpaper.utils.extensions.emptyString
import com.isanechek.wallpaper.view.main.fragments.category.CategoryFragment
import com.isanechek.wallpaper.view.main.fragments.timeline.TimelineFragment

sealed class NavigationId(val name: String = emptyString, val fullName: String = emptyString) {

    object TIMELINE : NavigationId("Timeline", TimelineFragment::class.java.name)
    object CATEGORY : NavigationId("Category", CategoryFragment::class.java.name)
    object ABOUT : NavigationId("APP INFO")
}