package com.isanechek.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpapers")
data class WallpaperEntity(val title: String,
                           @ColumnInfo(name = "key")
                           val publicKey: String,
                           @ColumnInfo(name = "path")
                           val publicPath: String,
                           @PrimaryKey
                           val id: String,
                           @ColumnInfo(name = "last_update_data")
                           val lastUpdateDate: String,
                           @ColumnInfo(name = "preview_url")
                           val previewUrl: String,
                           val category: String)