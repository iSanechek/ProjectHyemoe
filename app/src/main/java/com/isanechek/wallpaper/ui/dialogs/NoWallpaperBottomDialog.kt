package com.isanechek.wallpaper.ui.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.isanechek.extensions.onClick
import com.isanechek.wallpaper.ui.base.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.no_wallpaper_dialog_layout.*

@SuppressLint("ValidFragment")
class NoWallpaperBottomDialog constructor(private val callback: () -> Unit) : BaseBottomSheetDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(_layout.no_wallpaper_dialog_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        no_wallpaper_dialog_remove_btn.onClick {
            callback.invoke()
            dismiss()
        }

        no_wallpaper_dialog_cancel_btn.onClick {
            dismiss()
        }
    }
}