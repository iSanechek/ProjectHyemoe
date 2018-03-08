package com.isanechek.wallpaper.utils.extensions

import android.content.Context
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.widget.FrameLayout

infix fun Context.takeColor(colorId: Int) = ContextCompat.getColor(this, colorId)

val emptyString = ""

inline fun delay(milliseconds: Long, crossinline action: () -> Unit) {
    Handler().postDelayed({
        action()
    }, milliseconds)
}

fun <T> nonSafeLazy(initializer: () -> T): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        initializer()
    }
}

fun Int.toPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}

val Any?.isNull: Boolean
    get() = this == null