package com.isanechek.wallpaper.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.isanechek.common.models.Wallpaper
import com.isanechek.repository.Repository
import com.isanechek.repository.Request

/**
 * Created by isanechek on 9/26/17.
 */
class TimelineViewModel(
        private val repository: Repository<Wallpaper>
) : ViewModel() {

    private val _data = MutableLiveData<Request>()
    private val repoResult = Transformations
            .map(_data) {
                repository.listing(it)
            }

    val data: LiveData<PagedList<Wallpaper>> = Transformations
            .switchMap(repoResult) {
                it.pagedList
            }

    val networkState = Transformations
            .switchMap(repoResult) {
                it.networkState
            }

    val refreshState = Transformations
            .switchMap(repoResult) {
                it.refreshState
            }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    fun loadWallpapers2(key: String, path: String) {
        _data.postValue(Request(key, path))
    }
}