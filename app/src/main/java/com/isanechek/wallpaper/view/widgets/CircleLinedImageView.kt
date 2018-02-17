package com.isanechek.wallpaper.view.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.isanechek.wallpaper.utils._drawable
import com.isanechek.wallpaper.utils.extensions.toPx

class CircleLinedImageView : AppCompatImageView {

    private val LINE_WIDTH = 3F

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var padding = 17

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        with(paint) {
            color = Color.LTGRAY
            strokeWidth = LINE_WIDTH
            style = Paint.Style.STROKE
        }
        setImageResource(_drawable.ic_menu_gallery) //TODO remove
        padding = padding.toPx(context)
        setPadding(padding, padding, padding, padding)
    }

    override fun onDraw(canvas: Canvas) {
        val actualWidth = (width - LINE_WIDTH) / 2F
        canvas.drawCircle(width / 2F, width / 2F, actualWidth, paint)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}