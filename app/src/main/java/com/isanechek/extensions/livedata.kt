package com.isanechek.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations


fun <T, R> LiveData<T>.map(transform: (T) -> R): LiveData<R> = Transformations.map(this, transform)

fun <T, R> LiveData<T>.switchMap(transform: (T) -> LiveData<R>): LiveData<R> = Transformations.switchMap(this, transform)