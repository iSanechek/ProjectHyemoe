package com.isanechek.wallpaper.common

import com.isanechek.wallpaper.models.Update

interface PrefManager {
    var defaultAlbumKey: String
    var update: Update
    var time: Pair<String, String>
    var updateTime: Long
    var lastUpdateData: Long
}