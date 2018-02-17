package com.isanechek.wallpaper.data.repository

import android.arch.lifecycle.LiveData
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.RequestStrategy

interface Repository {
    suspend fun loadCategory(strategy: RequestStrategy)
    suspend fun loadImages(category: String, strategy: RequestStrategy)
    suspend fun loadImage(item: Wallpaper)
    suspend fun updateWallpaper(wallpaper: Wallpaper)
    fun loadWallpaper(id: String): LiveData<Wallpaper>
}