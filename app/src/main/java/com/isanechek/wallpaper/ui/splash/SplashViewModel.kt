package com.isanechek.wallpaper.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.isanechek.wallpaper.common.PrefManager
import com.isanechek.wallpaper.models.Info
import com.isanechek.repository.NetworkState
import com.isanechek.wallpaper.data.network.dto.Resource
import com.isanechek.repository.main.MainRepository
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SplashViewModel(private val pref: PrefManager,
                      private val repository: MainRepository) : ViewModel() {

    private val job = Job()

    val state: LiveData<NetworkState> = repository.network()

    val data: LiveData<Pair<Info, Resource>> = repository.resource()

    val preferences: PrefManager = pref

    fun loadData() = launch(UI + job) {
        repository.loadData()
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}