package com.isanechek.wallpaper.utils

import android.content.Context
import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.data.network.dto.ResourcePath
import com.isanechek.wallpaper.utils.extensions.emptyString

/**
 * Created by isanechek on 10/5/17.
 */
object HelperUtil {

    fun getStatusMessage(msg: String, context: Context): String = when (msg) {
        Const.STATUS_MSG_REQUEST_CATEGORY_NETWORK -> context.getString(R.string.request_category_network)
        Const.STATUS_MSG_LOAD_CATEGORY_NETWORK -> context.getString(R.string.load_category_network)
        Const.STATUS_MSG_WORKING_CATEGORY_NETWORK -> context.getString(R.string.working_category_network)
        Const.STATUS_MSG_SAVE_CATEGORY_NETWORK -> context.getString(R.string.save_category_network)
        Const.STATUS_MSG_LOAD_WALLPAPER_NETWORK -> context.getString(R.string.load_wallpaper_network)
        Const.STATUS_MSG_LOAD_WALLPAPERS_NETWORK -> context.getString(R.string.load_wallpapers_network)
        Const.STATUS_MSG_WORKING_WALLPAPER_NETWORK -> context.getString(R.string.working_wallpaper_network)
        Const.STATUS_MSG_WORKING_WALLPAPERS_NETWORK -> context.getString(R.string.working_wallpapers_network)
        Const.STATUS_MSG_SAVE_WALLPAPER_NETWORK -> context.getString(R.string.save_wallpaper_network)
        Const.STATUS_MSG_SAVE_WALLPAPERS_NETWORK -> context.getString(R.string.save_wallpapers_network)
        Const.STATUS_MSG_REQUEST_WALLPAPERS_NETWORK -> context.getString(R.string.request_wallpapers_network)
        Const.STATUS_MSG_REQUEST_WALLPAPER_NETWORK -> context.getString(R.string.request_wallpaper_network)
        else -> emptyString
    }
}