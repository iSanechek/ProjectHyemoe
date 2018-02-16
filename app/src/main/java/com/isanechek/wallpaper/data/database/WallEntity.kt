package com.isanechek.wallpaper.data.database

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.isanechek.wallpaper.utils.Const
import com.isanechek.wallpaper.utils.delegates.readBoolean
import com.isanechek.wallpaper.utils.delegates.writeBoolean
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * Created by isanechek on 8/27/17.
 */

@SuppressLint("ParcelCreator")
@Entity(tableName = Const.WALLPAPER_TABLE_NAME)
@Parcelize
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
                     var newItem: Boolean?) : Parcelable {
    @Ignore
    constructor(title: String, key: String, path: String) : this (
            title,
            key,
            path,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0L,
            false)

    companion object: Parceler<Wallpaper> {
        override fun create(parcel: Parcel): Wallpaper = Wallpaper(
                title = parcel.readString(),
                publicKey = parcel.readString(),
                publicPath = parcel.readString(),
                preview = parcel.readString(),
                cacheKey = parcel.readString(),
                fullCachePath = parcel.readString(),
                category = parcel.readString(),
                author = parcel.readString(),
                createDate = parcel.readString(),
                lastUpdate = parcel.readString(),
                size = parcel.readLong(),
                newItem = parcel.readBoolean()
        )

        override fun Wallpaper.write(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(publicKey)
            parcel.writeString(publicPath)
            parcel.writeString(preview)
            parcel.writeString(cacheKey)
            parcel.writeString(fullCachePath)
            parcel.writeString(category)
            parcel.writeString(author)
            parcel.writeString(createDate)
            parcel.writeString(lastUpdate)
            parcel.writeLong(size?:0L)
            parcel.writeBoolean(newItem?:false)
        }

    }
}

@SuppressLint("ParcelCreator")
@Entity(tableName = Const.CATEGORY_TABLE_NAME)
@Parcelize
data class Category(@PrimaryKey var title: String,
                    var publicKey: String?,
                    var publicPath: String?,
                    var createDate: String?,
                    var lastUpdate: String?,
                    var size: Long?,
                    var color: Int?,
                    var newItem: Boolean?) : Parcelable {

    @Ignore
    constructor(title: String, key: String, path: String) : this (
            title,
            key,
            path,
            null,
            null,
            null,
            null,
            null
    )

    companion object: Parceler<Category> {
        override fun create(parcel: Parcel): Category = Category(
                title = parcel.readString(),
                publicKey = parcel.readString(),
                publicPath = parcel.readString(),
                createDate = parcel.readString(),
                lastUpdate = parcel.readString(),
                size = parcel.readLong(),
                color = parcel.readInt(),
                newItem = parcel.readBoolean()
        )

        override fun Category.write(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(publicKey)
            parcel.writeString(publicPath)
            parcel.writeString(createDate)
            parcel.writeString(lastUpdate)
            parcel.writeLong(size?:0L)
            parcel.writeInt(color?:0)
            parcel.writeBoolean(newItem?:false)
        }
    }
}
