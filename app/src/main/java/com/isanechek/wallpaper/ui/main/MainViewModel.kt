package com.isanechek.wallpaper.ui.main

import androidx.lifecycle.ViewModel
import android.content.Context
import com.isanechek.wallpaper.common.UpdateAppHelper
import com.isanechek.repository.Response
import com.isanechek.wallpaper.models.Update
import com.isanechek.wallpaper.utils.SingleLiveEvent
import com.isanechek.wallpaper.ui.navigation.NavigationState
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by isanechek on 9/1/17.
 */
class MainViewModel(private val updateHelper: UpdateAppHelper) : ViewModel() {

    init {
//        startCheckUpdate()
    }

    private var state: NavigationState? = null

    fun saveNavState(state: NavigationState?) {
        this.state = state
    }

    fun getNavState(): NavigationState? = state

    val updateStatus: SingleLiveEvent<Response<Update>> = updateHelper.status()

    fun updateNow(ctx: Context, url: String) = launch(UI) {
        updateHelper.updateApk(ctx, url)
    }

    fun updateLater() {
        updateHelper.remindLater()
    }

    fun forgetThisUpdate() {

    }

    private fun startCheckUpdate() = launch(UI) {
        updateHelper.checkUpdate()
    }

}