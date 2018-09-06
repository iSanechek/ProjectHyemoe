package com.isanechek.wallpaper.common

import android.content.Context
import com.isanechek.repository.Response
import com.isanechek.wallpaper.models.Update
import com.isanechek.wallpaper.utils.SingleLiveEvent

interface UpdateAppHelper {
    suspend fun checkUpdate()
    suspend fun checkUpdateNow()
    suspend fun updateApk(ctx: Context, apkUrl: String)
    fun remindLater()
    fun status(): SingleLiveEvent<Response<Update>>
}