package com.isanechek.wallpaper.view.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.isanechek.wallpaper.data.Message
import com.isanechek.wallpaper.data.repository.YaRepository
import com.isanechek.wallpaper.view.navigation.NavigationState

/**
 * Created by isanechek on 9/1/17.
 */
class MainViewModel(private val repository: YaRepository) : ViewModel() {

    val openNavigationState: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }


    fun openNavigation() {
        openNavigationState.value = true
    }

    init {
        openNavigationState.value = false
    }
}