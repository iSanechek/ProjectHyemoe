package com.isanechek.wallpaper.view.details

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.repository.Repository

class DetailScreenViewModel(
        private val repository: Repository
) : ViewModel() {

    val data: LiveData<Wallpaper> = repository.loadWallpaperState()

    fun clearData() {
        repository.clearData()
    }
}