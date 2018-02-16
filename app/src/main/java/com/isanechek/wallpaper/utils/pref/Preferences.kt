package com.isanechek.wallpaper.utils.pref

import com.isanechek.wallpaper.App
import com.isanechek.wallpaper.utils.Const

/**
 * Created by isanechek on 7/13/17.
 */
open class Preferences {

    // Надо переписать, ибо это не совсе то что нужно

    open var defaultCategory: String
        get() = App.instance.getPreferences().getString("default_category", Const.EMPTY)
        set(value) { App.instance.getPreferences().edit().putString("default_category", value).apply() }
}