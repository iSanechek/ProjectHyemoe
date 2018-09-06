package com.isanechek.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.isanechek.storage.dao.CategoryDao
import com.isanechek.storage.dao.WallpaperDao
import com.isanechek.storage.entity.CategoryEntity
import com.isanechek.storage.entity.WallpaperEntity

@Database(entities = [
    (CategoryEntity::class),
    (WallpaperEntity::class)],
        version = 2,
        exportSchema = false
)
abstract class WallpapersDatabase : RoomDatabase() {
    abstract fun wallpaperDao(): WallpaperDao
    abstract fun categoryDao(): CategoryDao
}