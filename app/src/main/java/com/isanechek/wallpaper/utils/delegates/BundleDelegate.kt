package com.isanechek.wallpaper.utils.delegates

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.isanechek.extensions.isNull
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Chatikyan on 30.09.2017.
 */


inline fun <reified VALUE> bundle() = object : ReadOnlyProperty<androidx.fragment.app.Fragment, VALUE> {

    private var value: VALUE? = null

    override fun getValue(thisRef: androidx.fragment.app.Fragment, property: KProperty<*>): VALUE {
        if (value.isNull) {
            value = thisRef.arguments!![property.name] as VALUE
        }
        return value!!
    }
}

inline operator fun <reified V> Bundle.setValue(
    thisRef: Any?,
    property: KProperty<*>,
    value: V
): Bundle {
    put(property.name to thisRef)
    return this
}

inline fun <reified V> Bundle.put(pair: Pair<String, V>): Bundle {
    val value = pair.second
    val key = pair.first
    when (value) {
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is String -> putString(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        else -> throw UnsupportedOperationException("$value type not supported yet!!!")
    }
    return this
}

infix fun String.bundleWith(value: Any?): Bundle {
    val bundle = Bundle()
    val key = this
    bundle.put(key to value)
    return bundle
}


// for com.hanks.htextview.test

interface KParcelable : Parcelable {
    override fun writeToParcel(p0: Parcel, p1: Int)
    override fun describeContents(): Int = 0
}

// Create factory function
inline fun <reified T> parcelableCreator(
    crossinline create: (Parcel) -> T
) =
    object : Parcelable.Creator<T> {
        override fun newArray(p0: Int) = arrayOfNulls<T>(p0)
        override fun createFromParcel(p0: Parcel) = create(p0)

    }

inline fun <reified T> parcelableClassLoaderCreator(
    crossinline create: (Parcel, ClassLoader) -> T
) =
    object : Parcelable.ClassLoaderCreator<T> {
        override fun createFromParcel(p0: Parcel, p1: ClassLoader) = create(p0, p1)
        override fun createFromParcel(p0: Parcel) = create(p0, T::class.java.classLoader)
        override fun newArray(p0: Int) = arrayOfNulls<T>(p0)
    }

// Parcel extensions
inline fun Parcel.readBoolean() = readInt() != 0

inline fun Parcel.writeBoolean(value: Boolean) = writeInt(if (value) 1 else 0)

inline fun <reified T : Enum<T>> Parcel.readEnum() =
    readInt().let { if (it >= 0) enumValues<T>()[it] else null }

inline fun <T : Enum<T>> Parcel.writeEnum(value: T?) =
    writeInt(value?.ordinal ?: -1)

inline fun <T> Parcel.readNullable(reader: () -> T) =
    if (readInt() != 0) reader() else null

inline fun <T> Parcel.writeNullable(value: T?, writer: (T) -> Unit) {
    if (value != null) {
        writeInt(1)
        writer(value)
    } else {
        writeInt(0)
    }
}

fun Parcel.readDate() =
    readNullable { Date(readLong()) }

fun Parcel.writeDate(value: Date?) =
    writeNullable(value) { writeLong(it.time) }

fun Parcel.readBigInteger() =
    readNullable { BigInteger(createByteArray()) }

fun Parcel.writeBigInteger(value: BigInteger?) =
    writeNullable(value) { writeByteArray(it.toByteArray()) }

fun Parcel.readBigDecimal() =
    readNullable { BigDecimal(BigInteger(createByteArray()), readInt()) }

fun Parcel.writeBigDecimal(value: BigDecimal?) = writeNullable(value) {
    writeByteArray(it.unscaledValue().toByteArray())
    writeInt(it.scale())
}

fun <T : Parcelable> Parcel.readTypedObjectCompat(c: Parcelable.Creator<T>) =
    readNullable { c.createFromParcel(this) }

fun <T : Parcelable> Parcel.writeTypedObjectCompat(value: T?, parcelableFlags: Int) =
    writeNullable(value) { it.writeToParcel(this, parcelableFlags) }