package com.isanechek.wallpaper.view.main.fragments.timeline

import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import com.isanechek.wallpaper.data.database.DataBase
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.data.repository.YaRepository
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by isanechek on 9/26/17.
 */
class TimelineViewModel(private val repository: YaRepository,
                        val database: DataBase) : ViewModel() {

    fun loadWallpapers(category: String, startPosition: Int = 0) =
            LivePagedListBuilder(
                    database
                            .wallpaper()
                            .loadAllPagedWallpapers(category),
                    20)
                    .build()

    fun load(category: String, strategy: RequestStrategy) {
        launch(UI) {
            repository.loadImages(category, strategy)
        }
    }
}