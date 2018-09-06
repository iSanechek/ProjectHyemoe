package com.isanechek.wallpaper.ui.navigation

data class NavigationState constructor(
    var activeTag: String? = null,
    var firstTag: String? = null,
    var isCustomAnimationUsed: Boolean = false
) {
    fun clear() {
        activeTag = null
        firstTag = null
    }
}