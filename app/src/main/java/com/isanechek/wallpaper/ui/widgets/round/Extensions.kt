package com.isanechek.wallpaper.ui.widgets.round

import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import androidx.annotation.RequiresApi
import android.view.View
import android.view.ViewOutlineProvider

internal fun View.updateOutlineProvider(cornerRadius: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        outlineProvider = RoundOutlineProvider(cornerRadius)
    }
}

internal class RoundOutlineProvider(private val cornerRadius: Float) : ViewOutlineProvider() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutline(view: View, outline: Outline) {
        val rect = Rect(0, 0, view.measuredWidth, view.measuredHeight)
        outline.setRoundRect(rect, cornerRadius)
    }
}