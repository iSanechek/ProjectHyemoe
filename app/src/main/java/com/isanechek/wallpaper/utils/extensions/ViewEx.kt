package com.isanechek.wallpaper.utils.extensions

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.onClick(function: () -> Unit) {
    setOnClickListener {
        function()
    }
}

var View.scale: Float
    get() = Math.min(scaleX, scaleY)
    set(value) {
        scaleY = value
        scaleX = value
    }

fun ImageView.tint(colorId: Int) {
    setColorFilter(context.takeColor(colorId), PorterDuff.Mode.SRC_IN)
}

infix fun ViewGroup.inflate(layoutResId: Int): View =
        LayoutInflater.from(context).inflate(layoutResId, this, false)