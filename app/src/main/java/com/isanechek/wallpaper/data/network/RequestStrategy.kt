package com.isanechek.wallpaper.data.network

/**
 * Created by isanechek on 11/19/17.
 */
sealed class RequestStrategy {
    object EMPTY_REQUEST : RequestStrategy()
    object DATA_REQUEST : RequestStrategy()
    object UPDATE_REQUEST : RequestStrategy()
}