package com.isanechek.wallpaper.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by isanechek on 9/15/17.
 */
@Database(entities = [(Category::class), (Wallpaper::class)], version = 5)
abstract class DataBase : RoomDatabase() {
    abstract fun wallpaper(): WallDao
}
