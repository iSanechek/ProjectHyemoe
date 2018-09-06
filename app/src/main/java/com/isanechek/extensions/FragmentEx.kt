package com.isanechek.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.ContextCompat
import java.io.Serializable

infix fun androidx.fragment.app.Fragment.takeColor(colorId: Int) = context?.let { ContextCompat.getColor(it, colorId) }

fun androidx.fragment.app.Fragment.withArgument(key: String, value: Any) {
    val args = Bundle()
    when (value) {
        is Int -> args.putInt(key, value)
        is Long -> args.putLong(key, value)
        is String -> args.putString(key, value)
        is Parcelable -> args.putParcelable(key, value)
        is Serializable -> args.putSerializable(key, value)
        else -> throw UnsupportedOperationException("${value.javaClass.simpleName} type not supported yet!!!")
    }
    arguments = args
}

inline infix fun <reified T> androidx.fragment.app.Fragment.extraWithKey(key: String): T {
    val value: Any = arguments!![key]
    return value as T
}