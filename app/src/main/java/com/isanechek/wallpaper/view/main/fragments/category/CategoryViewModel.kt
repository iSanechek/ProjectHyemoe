package com.isanechek.wallpaper.view.main.fragments.category

import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import com.isanechek.wallpaper.data.database.DataBase
import com.isanechek.wallpaper.data.network.RequestStrategy
import com.isanechek.wallpaper.data.repository.YaRepository

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
        repository.loadCategory(strategy)
    }
}