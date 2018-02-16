package com.isanechek.wallpaper.utils.extensions

import android.app.Activity
import android.support.v4.content.ContextCompat

infix fun Activity.takeColor(colorId: Int) = ContextCompat.getColor(this, colorId)