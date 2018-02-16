package com.isanechek.wallpaper.view.custom.vega

import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 定位到第一个子View的SnapHelper
 * Created by xmuSistone on 2017/9/19.
 */
class StartSnapHelper : LinearSnapHelper() {

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager,
                                              targetView: View): IntArray? {
        val out = IntArray(2)
        out[0] = 0
        out[1] = (layoutManager as VegaLayoutManager).snapHeight
        return out
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        val custLayoutManager = layoutManager as VegaLayoutManager
        return custLayoutManager.findSnapView()
    }
}