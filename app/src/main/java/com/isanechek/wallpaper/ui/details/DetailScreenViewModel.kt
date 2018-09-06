package com.isanechek.wallpaper.ui.details

import android.app.WallpaperManager
import android.content.Context
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.isanechek.common.DebugUtils
import com.isanechek.common.models.Wallpaper
import com.isanechek.wallpaper.BuildConfig
import com.isanechek.wallpaper.common.DownloadHelper
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.File

class DetailScreenViewModel(
        private val context: Context,
        private val debug: DebugUtils,
        private val bitmapDownload: DownloadHelper
) : ViewModel() {

    private val job: Job = Job()

    override fun onCleared() {
        if (job.isActive) {
            job.cancel()
        }
        super.onCleared()
    }

    fun loadWallpaper(wallpaper: Wallpaper) =
            launch(UI + job) {
                val path = async {
                    bitmapDownload.downloadBitmap(wallpaper.downloadUrl)
                }.await()
                val uri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        File(path)
                )
                val wm = WallpaperManager.getInstance(context)
                context.startActivity(wm.getCropAndSetWallpaperIntent(uri))
            }

    /*
    * Need rewrite with clear temp bitmap
    */
    fun stopDownloading() {
        if (job.isActive) {
            job.cancel()
        }
    }
}