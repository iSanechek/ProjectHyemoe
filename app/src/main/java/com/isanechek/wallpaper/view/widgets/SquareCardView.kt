package com.isanechek.wallpaper.view.widgets

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet

class SquareCardView constructor(context: Context, attrs: AttributeSet? = null) : CardView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}