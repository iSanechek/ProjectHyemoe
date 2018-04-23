package com.isanechek.wallpaper.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.isanechek.wallpaper.utils.Const
import kotlinx.android.parcel.Parcelize

/**
 * Created by isanechek on 8/27/17.
 */

@Parcelize
@Entity(tableName = Const.WALLPAPER_TABLE_NAME)
data class Wallpaper(@PrimaryKey var title: String,
                     var publicKey: String?,
                     var publicPath: String?,
                     var preview: String?,
                     var cacheKey: String?,
                     var fullCachePath: String?,
                     var category: String?,
                     var author: String?,
                     var createDate: String?,
                     var lastUpdate: String?,
                     var size: Long?,
                     var newItem: Boolean?) : Parcelable

@Parcelize
@Entity(tableName = Const.CATEGORY_TABLE_NAME)
data class Category(@PrimaryKey var title: String,
                    var publicKey: String?,
                    var publicPath: String?,
                    var createDate: String?,
                    var lastUpdate: String?,
                    var size: Long?,
                    var color: Int?,
                    var cover: String?,
                    var newItem: Boolean?) : Parcelable