package com.isanechek.wallpaper.ui.base

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetDialog : BottomSheetDialogFragment() {

    override fun getTheme(): Int = _style.BottomSheetDialogTheme
}