package com.isanechek.wallpaper.utils

import android.os.Build
import android.os.Looper

val isUiThread = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() == Looper.getMainLooper().thread