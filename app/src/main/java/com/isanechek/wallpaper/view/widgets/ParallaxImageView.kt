package com.isanechek.wallpaper.view.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.isanechek.wallpaper.R


class ParallaxImageView : android.support.v7.widget.AppCompatImageView {

    private val MAX_PARALLAX_OFFSET = context.resources.getDimension(R.dimen.parallax_image_view_offset).toInt()

    private var recyclerView_height = -1
    private val recyclerView_location = intArrayOf(-1, -1)

    internal var attached = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec + context.resources.getDimension(R.dimen.parallax_image_view_offset).toInt())
//
//        val width = widthMeasureSpec
//
//        //force a 16:9 aspect ratio
//        val height = Math.round(width * .5625f)
//        setMeasuredDimension(width, height)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        attached = true

        val view = rootView.findViewWithTag<View>(RECYCLER_VIEW_TAG)
        if (view is RecyclerView) {
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!attached) {
                        recyclerView!!.removeOnScrollListener(this)
                        return
                    }

                    if (recyclerView_height == -1) {
                        recyclerView_height = recyclerView!!.height
                        recyclerView.getLocationOnScreen(recyclerView_location)
                    }

                    setParallaxTranslation()
                }
            })
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        attached = false
    }

    fun setParallaxTranslation() {
        if (recyclerView_height == -1) {
            return
        }

        val location = IntArray(2)
        getLocationOnScreen(location)

        val visible = location[1] + height > recyclerView_location[1] || location[1] < recyclerView_location[1] + recyclerView_height

        if (!visible) {
            return
        }

        val dy = (location[1] - recyclerView_location[1]).toFloat()

        val translationY = MAX_PARALLAX_OFFSET * dy / recyclerView_height.toFloat()
        setTranslationY(-translationY)
    }

    companion object {

        val RECYCLER_VIEW_TAG = "RECYCLER_VIEW_TAG"
    }
}
