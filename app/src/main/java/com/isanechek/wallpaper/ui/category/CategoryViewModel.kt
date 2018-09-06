package com.isanechek.wallpaper.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.isanechek.common.models.Category
import com.isanechek.repository.Repository
import com.isanechek.wallpaper.common.PrefManager
import com.isanechek.repository.Request
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by isanechek on 9/26/17.
 */
class CategoryViewModel(
        private val repo: Repository<Category>,
        private val pref: PrefManager
) : ViewModel() {

    private val job = Job()
    private val _data = MutableLiveData<Pair<String, String>>()
    fun loadData(key: String, path: String) = launch(
            context = UI,
            parent = job) {
        pref.time = Pair(key, path)
        _data.value = Pair(key, path)
        repo.loadData(Request(key, path))
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }

    private val repoResult = Transformations
            .map(_data) {
                repo.listing(null)
            }

    val data: LiveData<PagedList<Category>> = Transformations
            .switchMap(repoResult) {
                it.pagedList
            }

    val networkState = Transformations
            .switchMap(repoResult) {
                it.networkState
            }!!

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
}