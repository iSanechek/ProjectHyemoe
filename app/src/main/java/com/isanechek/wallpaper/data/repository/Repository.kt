package com.isanechek.wallpaper.data.repository

import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.RequestStrategy

interface Repository {
    fun loadCategory(strategy: RequestStrategy)
    fun loadImages(category: String, strategy: RequestStrategy)
    fun loadImage(item: Wallpaper)
}