package com.isanechek.wallpaper.view.custom.vega

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup

/**
 * Created by xmuSistone on 2017/9/20.
 */
class VegaLayoutManager : RecyclerView.LayoutManager() {

    private var mDecoratedMeasuredWidth: Int = 0
    private var mDecoratedMeasuredHeight: Int = 0
    private var scroll = 0
    private val locationRects = SparseArray<Rect>()
    private val attachedItems = SparseBooleanArray()
    private var needSnap = false
    private var firstLayoutChildren = true
    private var lastDy = 0
    private var maxScroll = -1

    // scroll变大，属于列表往下走，往下找下一个为snapView
    val snapHeight: Int
        get() {
            if (!needSnap) {
                return 0
            }
            needSnap = false

            val displayRect = Rect(0, scroll, width, height + scroll)
            val itemCount = itemCount
            for (i in 0 until itemCount) {
                val itemRect = locationRects.get(i)
                if (displayRect.intersect(itemRect)) {

                    if (lastDy > 0) {
                        if (i < itemCount - 1) {
                            val nextRect = locationRects.get(i + 1)
                            return nextRect.top - displayRect.top
                        }
                    }
                    return itemRect.top - displayRect.top
                }
            }
            return 0
        }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (!firstLayoutChildren) {
            return
        }
        firstLayoutChildren = false

        val itemCount = itemCount
        if (itemCount <= 0 || state!!.isPreLayout) {
            return
        }

        if (childCount == 0) {
            // 通过第一个itemView，获取一些中间变量
            val itemView = recycler!!.getViewForPosition(0)
            addView(itemView)
            measureChildWithMargins(itemView, 0, 0)
            mDecoratedMeasuredWidth = getDecoratedMeasuredWidth(itemView)
            mDecoratedMeasuredHeight = getDecoratedMeasuredHeight(itemView)
        }

        var tempPosition = paddingTop
        for (i in 0 until itemCount) {
            val rect = Rect()
            rect.left = paddingLeft
            rect.top = tempPosition
            rect.right = mDecoratedMeasuredWidth - paddingRight
            rect.bottom = rect.top + mDecoratedMeasuredHeight
            locationRects.put(i, rect)
            attachedItems.put(i, false)

            tempPosition = tempPosition + mDecoratedMeasuredHeight
        }

        // 得到中间变量后，第一个View先回收放到缓存，后面会再次统一layout
        detachAndScrapAttachedViews(recycler)
        layoutItemsOnCreate(recycler)
        computeMaxScroll()
    }


    /**
     * 对外提供接口，找到第一个可视view的index
     */
    fun findFirstVisibleItemPosition(): Int {
        val count = locationRects.size()
        val displayRect = Rect(0, scroll, width, height + scroll)
        for (i in 0 until count) {
            if (Rect.intersects(displayRect, locationRects.get(i)) && attachedItems.get(i)) {
                return i
            }
        }
        return 0
    }

    /**
     * 计算可滑动的最大值
     */
    private fun computeMaxScroll() {
        maxScroll = locationRects.get(locationRects.size() - 1).bottom - height
        if (maxScroll < 0) {
            maxScroll = 0
            return
        }

        val childCount = childCount
        var screenFilledHeight = 0
        for (i in childCount - 1 downTo 0) {
            val rect = locationRects.get(i)
            screenFilledHeight += rect.bottom - rect.top
            if (screenFilledHeight > height) {
                val extraSnapHeight = height - (screenFilledHeight - (rect.bottom - rect.top))
                maxScroll += extraSnapHeight
                break
            }
        }
    }

    /**
     * 初始化的时候，layout子View
     */
    private fun layoutItemsOnCreate(recycler: RecyclerView.Recycler?) {
        val itemCount = itemCount

        for (i in 0 until itemCount) {
            val childView = recycler?.getViewForPosition(i)
            addView(childView)
            measureChildWithMargins(childView, 0, 0)
            childView?.let { layoutItem(it, locationRects.get(i)) }
            attachedItems.put(i, true)
            childView?.pivotY = 0f
            childView?.pivotX = (childView!!.measuredWidth / 2).toFloat()

            if (locationRects.get(i).top > height) {
                break
            }
        }
    }


    /**
     * 初始化的时候，layout子View
     */
    private fun layoutItemsOnScroll(recycler: RecyclerView.Recycler?, state: RecyclerView.State?, dy: Int) {
        val childCount = childCount
        if (state!!.isPreLayout || childCount == 0) {
            return
        }

        // 1. 已经在屏幕上显示的child
        val itemCount = itemCount
        val displayRect = Rect(0, scroll, width, height + scroll)
        for (i in 0 until childCount) {
            val child = getChildAt(i) ?: continue
            val position = getPosition(child)
            if (!Rect.intersects(displayRect, locationRects.get(position))) {
                // 回收滑出屏幕的View
                removeAndRecycleView(child, recycler!!)
                attachedItems.put(position, false)
            } else {
                // Item还在显示区域内，更新滑动后Item的位置
                layoutItem(child, locationRects.get(position)) //更新Item位置
            }
        }

        // 2. 复用View添加
        for (i in 0 until itemCount) {
            if (Rect.intersects(displayRect, locationRects.get(i)) && !attachedItems.get(i)) {
                // 重新加载可见范围内的Item
                val scrap = recycler!!.getViewForPosition(i)
                measureChildWithMargins(scrap, 0, 0)
                scrap.pivotY = 0f
                scrap.pivotX = (scrap.measuredWidth / 2).toFloat()
                if (dy > 0) {
                    addView(scrap)
                } else {
                    addView(scrap, 0)
                }
                // 将这个Item布局出来
                layoutItem(scrap, locationRects.get(i))
                attachedItems.put(i, true)
            }
        }
    }

    private fun layoutItem(child: View, rect: Rect) {
        val topDistance = scroll - rect.top
        val layoutTop: Int
        val layoutBottom: Int
        if (topDistance in 0..mDecoratedMeasuredHeight - 1) {
            val rate1 = topDistance.toFloat() / mDecoratedMeasuredHeight
            val rate2 = 1 - rate1 * rate1 / 3
            val rate3 = 1 - rate1 * rate1
            child.scaleX = rate2
            child.scaleY = rate2
            child.alpha = rate3

            layoutTop = 0
            layoutBottom = mDecoratedMeasuredHeight
        } else {
            child.scaleX = 1f
            child.scaleY = 1f
            child.alpha = 1f

            layoutTop = rect.top - scroll
            layoutBottom = rect.bottom - scroll
        }
        layoutDecorated(child, rect.left, layoutTop, rect.right, layoutBottom)
    }

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (itemCount == 0 || dy == 0) {
            return 0
        }
        var travel = dy
        if (dy + scroll < 0) {
            travel = -scroll
        } else if (dy + scroll > maxScroll) {
            travel = maxScroll - scroll
        }
        scroll += travel //累计偏移量
        lastDy = dy
        layoutItemsOnScroll(recycler, state, dy)
        return travel
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        StartSnapHelper().attachToRecyclerView(view)
    }

    override fun onScrollStateChanged(state: Int) {
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            needSnap = true
        }
        super.onScrollStateChanged(state)
    }

    fun findSnapView(): View? {
        return if (childCount > 0) {
            getChildAt(0)
        } else null
    }
}