package com.isanechek.wallpaper.utils

import android.app.WallpaperManager
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import java.io.File
import java.io.IOException
import android.content.Intent
import android.net.Uri


/**
 * Created by isanechek on 2/23/18.
 */
object WallpaperUtils {

    fun setAsWallpaper(context: Context, setAs: String, uri: Uri) = when (setAs) {
        "System" -> Intent(Intent.ACTION_ATTACH_DATA).run {
            addCategory(Intent.CATEGORY_DEFAULT)
            setDataAndType(uri, "image/jpeg")
            putExtra("mimeType", "image/jpeg")
            context.startActivity(Intent.createChooser(this, "Set wallpaper with"))
        }
        else -> setWallpaper(context, File(uri.path))
    }

    private fun setWallpaper(context: Context, lastFile: File) {

        val height = getScreenHeight()
        val width = getScreenWidth() shl 1

        val imagePath = lastFile.absolutePath

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        val decodedSampleBitmap = BitmapFactory.decodeFile(imagePath, options)

        val wm = WallpaperManager.getInstance(context)
        try {
            wm.setBitmap(decodedSampleBitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            lastFile.delete()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    private fun getScreenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

    private fun getScreenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels
}