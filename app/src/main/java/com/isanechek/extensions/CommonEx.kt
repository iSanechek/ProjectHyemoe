package com.isanechek.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.widget.Toast

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

fun Activity.getResourceId(@AttrRes attribute: Int) : Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.resourceId
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

fun Context.alertDialog(
        title: CharSequence? = null,
        message: CharSequence? = null,
        positiveText: CharSequence? = null,
        positiveEvent: (() -> Unit)? = null,
        negativeText: CharSequence? = null,
        negativeEvent: (() -> Unit)? = null

): AlertDialog.Builder = AlertDialog.Builder(this).apply {
    title?.let { setTitle(title) }
    message?.let { setMessage(message) }
    positiveText?.let {
        setPositiveButton(positiveText) { _, _ ->
            positiveEvent?.let { positiveEvent() }
        }
    }
    negativeText?.let {
        setNegativeButton(negativeText) { _, _ ->
            negativeEvent?.let { negativeEvent() }
        }
    }
}

fun androidx.fragment.app.Fragment.toast(text: String) = activity?.toast(text)

fun androidx.fragment.app.Fragment.toast(@StringRes resId: Int) = activity?.toast(resId)
fun Context.toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
fun Context.toast(@StringRes resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun androidx.fragment.app.Fragment.longToast(text: String) = activity?.longToast(text)
fun androidx.fragment.app.Fragment.longToast(@StringRes resId: Int) = activity?.longToast(resId)
fun Context.longToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
fun Context.longToast(@StringRes resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_LONG).show()