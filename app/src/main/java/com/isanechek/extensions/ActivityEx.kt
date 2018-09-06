package com.isanechek.extensions

import android.app.Activity
import androidx.core.content.ContextCompat

infix fun Activity.takeColor(colorId: Int) = ContextCompat.getColor(this, colorId)