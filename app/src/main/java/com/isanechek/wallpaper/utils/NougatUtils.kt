package com.isanechek.wallpaper.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object NougatUtils {

    fun formatFileProviderIntent(ctx: Context, file: File, intent: Intent, intentType: String): Intent {
        val uri = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".fileprovider", file)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(uri, intentType)
        return intent
    }

    fun formatFileProviderUri(ctx: Context, file: File): Uri =
            FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".fileprovider", file)
}