package com.isanechek.wallpaper.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * Created by isanechek on 2/27/18.
 */


class NotificationUtil(private val context: Context) {

    private val nm: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        @TargetApi(Build.VERSION_CODES.O)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Wallpaper", NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.description = "Wallpaper"
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.importance = NotificationManager.IMPORTANCE_LOW
            nm.createNotificationChannel(notificationChannel)
        }
    }

    fun cancelNotification(downloadUri: Uri) {
        nm.cancel(downloadUri.hashCode())
    }

    fun showProgressNotification(title: String, content: String, progress: Int, downloadUri: Uri) {
        val id = downloadUri.hashCode()
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(_drawable.vector_ic_file_download)
            .setProgress(100, progress, false)
        injectAppIntent(builder)
    }

    fun showCompleteNotification(downloadUri: Uri, filePath: String?) {
        val id = downloadUri.hashCode()
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(_string.notif_title_download_complited))
            .setContentText(context.getString(_string.notif_tap_to_open))
            .setAutoCancel(false)
            .setSmallIcon(_drawable.ic_cloud_done_black_24dp)
        if (filePath == null) injectAppIntent(builder) else {
            injectViewIntent(builder, filePath)
        }
    }

    private fun injectAppIntent(builder: NotificationCompat.Builder) {
//        Intent(context, DetailsActivity::class.java).run {
//            builder.setContentIntent(
//                PendingIntent.getActivity(
//                    context,
//                    0,
//                    this,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//            )
//        }
    }

    private fun injectViewIntent(builder: NotificationCompat.Builder, filePath: String) {
//        Intent(context, DetailsActivity::class.java).run {
//            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(filePath)))
//            builder.setContentIntent(
//                PendingIntent.getActivity(
//                    context,
//                    0,
//                    this,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//            )
//        }
    }

    companion object {
        private const val TAG = "NotificationUtil"
        private const val NOTIFICATION_CHANNEL_ID = "Wallpaper"
    }
}