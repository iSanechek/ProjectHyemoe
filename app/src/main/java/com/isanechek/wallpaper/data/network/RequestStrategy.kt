package com.isanechek.wallpaper.data.network

import com.isanechek.wallpaper.utils.Experimental

/**
 * Created by isanechek on 11/19/17.
 */
@Experimental
sealed class RequestStrategy {
    object EMPTY_REQUEST : RequestStrategy()
    object DATA_REQUEST : RequestStrategy()
    object UPDATE_REQUEST : RequestStrategy()
}