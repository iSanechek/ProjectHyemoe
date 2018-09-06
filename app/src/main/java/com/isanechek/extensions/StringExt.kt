package com.isanechek.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Long.toStringData(): String = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US).format(Date())