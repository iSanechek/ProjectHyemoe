package com.isanechek.wallpaper.data

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.repository.Repository
import com.isanechek.wallpaper.utils.logger
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject

class DownloadService : IntentService("Wall_Service") {
    private val repository by inject<Repository>()

    override fun onHandleIntent(p0: Intent?) {
        val args = p0?.getBundleExtra(ARGS)
        val wallpaper = args?.getParcelable(ARGS_WALLPAPER) as Wallpaper
        logger("$TAG url ${wallpaper.title}")
        launch(CommonPool) {
            repository.loadImage(wallpaper)
        }
    }

    companion object {
        private const val TAG = "Service"
        private const val ARGS = "downloads.args"
        private const val ARGS_WALLPAPER = "downloads.wallpaper"
        fun startDownloads(ctx: Context, wallpaper: Wallpaper) {
            Intent(ctx, DownloadService::class.java).run {
                putExtra(ARGS, Bundle().apply {
                    putParcelable(ARGS_WALLPAPER, wallpaper)
                })
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ctx.startForegroundService(this)
                } else ctx.startService(this)
            }
        }
    }
}