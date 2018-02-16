package com.isanechek.wallpaper.view.main.fragments.details

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.Target
import com.isanechek.wallpaper.data.database.DataBase
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.Response
import com.isanechek.wallpaper.data.repository.YaRepository
import com.isanechek.wallpaper.utils.Const
import com.isanechek.wallpaper.utils.extensions.async
import com.isanechek.wallpaper.utils.logger
import java.io.File
import java.io.FileOutputStream

/**
 * Created by isanechek on 11/20/17.
 */
class DetailsViewModel(private val repository: YaRepository,
                       val database: DataBase) : ViewModel() {
    private val _uri: MutableLiveData<Response<Uri>> by lazy {
        MutableLiveData<Response<Uri>>()
    }
    val uri: LiveData<Response<Uri>> = _uri
    val status: LiveData<Response<Any>> = repository.status

    fun getDownloadUrl(item: Wallpaper) {
        repository.loadImage(item)
    }

    fun getBitmapPath(url: String, ctx: Context) = async {
        _uri.value = Response.loading(message = Const.REQUEST_BITMAP)
        val fileName = Uri.parse(url).lastPathSegment
        val uriPath = await {
            val future: FutureTarget<Bitmap> = Glide
                    .with(ctx)
                    .asBitmap()
                    .load(url)
                    .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            val bitmap: Bitmap = future.get()
            val file = File(ctx.getExternalFilesDir("Files"), "$fileName.jpg")
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }
            Uri.fromFile(file)
        }
        _uri.value = Response.success(uriPath, "")
    }.onError {
        logger("herak ${it.message}")
        _uri.value = Response.error(message = it.message?: "HZ Error")
    }

    fun updateWallpaper(item: Wallpaper) = async {
        await {
            database.wallpaper().updateWallpaper(item)
        }
    }

    fun getWallObj(id: String) : LiveData<Wallpaper> = database.wallpaper().loadWallpaper(id)

    fun empty() {

    }
}