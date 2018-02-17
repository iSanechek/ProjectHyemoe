package com.isanechek.wallpaper.view.main.fragments.category

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
class CategoryViewModel(private val repository: YaRepository,
                        val database: DataBase) : ViewModel() {

    val loadCategory = LivePagedListBuilder(
            database.wallpaper()
                    .loadAllPagedCategory(), 10)
            .build()

    fun load(strategy: RequestStrategy) {
        launch(UI) {
            repository.loadCategory(strategy)
        }
    }
}