package com.isanechek.common.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wallpaper(val title: String,
                     val publicKey: String,
                     val publicPath: String,
                     val id: String,
                     val lastUpdateDate: String,
                     val previewUrl: String,
                     val downloadUrl: String,
                     val category: String,
                     val size: Long) : Parcelable