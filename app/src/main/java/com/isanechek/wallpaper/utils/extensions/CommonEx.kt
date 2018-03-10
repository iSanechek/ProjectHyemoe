package com.isanechek.wallpaper.utils.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
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

inline fun fromApi(fromVersion: Int, inclusive: Boolean = true, action: () -> Unit) {
    if (Build.VERSION.SDK_INT > fromVersion || (inclusive && Build.VERSION.SDK_INT == fromVersion)) action()
}

fun isApi(version: Int): Boolean = Build.VERSION.SDK_INT >= version

val lollipop = Build.VERSION_CODES.LOLLIPOP
val marshmallow = Build.VERSION_CODES.M
val nougat = Build.VERSION_CODES.N_MR1
val oreo = Build.VERSION_CODES.O
