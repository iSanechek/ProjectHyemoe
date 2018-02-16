package com.isanechek.wallpaper.view.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.isanechek.wallpaper.data.Message
import com.isanechek.wallpaper.view.navigation.NavigationState

/**
 * Created by isanechek on 9/1/17.
 */
class MainViewModel : ViewModel() {
    private val mutableNavigationState: MutableLiveData<NavigationState> by lazy { MutableLiveData<NavigationState>() }

    init {
        mutableNavigationState.value = null
    }

    fun sendMessage(message: Message) {
    }

    fun saveNavigationState(state: NavigationState) { mutableNavigationState.value = state }

    fun getNavigationState() : LiveData<NavigationState> = mutableNavigationState
}