package com.isanechek.wallpaper.view.widgets.round

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
import com.isanechek.wallpaper.R

class RoundKornerFrameLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {
    private val canvasRounder: CanvasRounder

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundKornerFrameLayout, 0, 0)
        val cornerRadius = array.getDimension(R.styleable.RoundKornerFrameLayout_corner_radius, 0f)
        array.recycle()
        canvasRounder = CanvasRounder(cornerRadius)
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null)
//        }
        updateOutlineProvider(cornerRadius)
    }

    override fun onSizeChanged(currentWidth: Int, currentHeight: Int, oldWidth: Int, oldheight: Int) {
        super.onSizeChanged(currentWidth, currentHeight, oldWidth, oldheight)
        canvasRounder.updateSize(currentWidth, currentHeight)
    }

    override fun draw(canvas: Canvas) = canvasRounder.round(canvas) { super.draw(it) }

    override fun dispatchDraw(canvas: Canvas) = canvasRounder.round(canvas) { super.dispatchDraw(it)}

    fun setCornerRadius(cornerRadius: Float) {
        canvasRounder.cornerRadius = cornerRadius
        updateOutlineProvider(cornerRadius)
        invalidate()
    }
}
