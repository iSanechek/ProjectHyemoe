package com.isanechek.wallpaper.view.main.fragments.details

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.isanechek.wallpaper.data.database.Wallpaper
import com.isanechek.wallpaper.data.network.Response
import com.isanechek.wallpaper.data.repository.YaRepository

/**
 * Created by isanechek on 11/20/17.
 */
class DetailsViewModel(private val repository: YaRepository) : ViewModel() {

    fun getWallpaper(id: String): LiveData<Wallpaper> = repository.loadWallpaper(id)

    val status: LiveData<Response<Any>> = repository.status

}