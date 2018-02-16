package com.isanechek.wallpaper.view.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by Alessandro on 03/03/2017.
 * http://stackoverflow.com/questions/40494623/android-imageview-169
 */

class CustomImageView : ImageView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth

        //force a 16:9 aspect ratio
        val height = Math.round(width * .5625f)
        setMeasuredDimension(width, height)
    }

}