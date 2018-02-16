package com.isanechek.wallpaper.view.base

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import com.isanechek.wallpaper.utils.L

/**
 * Created by isanechek on 11/20/17.
 */
class BaseListFragment : BaseFragment() {

    lateinit var refresh: SwipeRefreshLayout
    lateinit var timeline: RecyclerView

    override fun layoutResId(): Int = L.fragment_list_layout

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}