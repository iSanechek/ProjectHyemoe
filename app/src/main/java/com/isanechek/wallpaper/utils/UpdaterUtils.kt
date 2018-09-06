package com.isanechek.wallpaper.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.isanechek.wallpaper.BuildConfig
import java.io.File


object UpdaterUtils {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun appVersion(ctx: Context): Int {
        var version = 0
        try {
            version = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version

    }

    fun installApkAsFile(context: Context, filePath: File?) {
        if (filePath != null) {
            var intent = Intent(Intent.ACTION_VIEW)
            if (Build.VERSION.SDK_INT >= 24) {
                intent = NougatUtils.formatFileProviderIntent(context, filePath, intent, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(Uri.fromFile(filePath), "application/vnd.android.package-archive")
            }
            context.startActivity(intent)
        } else {
            if (BuildConfig.DEBUG) {
                Log.v("Install Apk", "apk update not found")
            }
        }
    }

    fun isUpdateAvailable(versionOld: Int, versionNew: Int): Boolean = versionNew > versionOld

    fun isUpdateAvailable(versionOld: String, versionNew: String): Boolean {
        var res = false
        if (versionOld != "0.0.0.0" && versionNew != "0.0.0.0") {
            res = versionCompare(versionOld, versionNew) < 0
        }
        return res
    }

    private fun versionCompare(versionOld: String, versionNew: String): Int {

        val vals1 = versionOld.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val vals2 = versionNew.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var i = 0

        while (i < vals1.size && i < vals2.size && vals1[i] == vals2[i]) {
            i++
        }

        if (i < vals1.size && i < vals2.size) {
            val diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]))
            return Integer.signum(diff)
        }

        return Integer.signum(vals1.size - vals2.size)
    }

}