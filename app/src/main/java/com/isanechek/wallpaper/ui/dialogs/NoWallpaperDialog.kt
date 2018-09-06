package com.isanechek.wallpaper.ui.dialogs

import android.annotation.SuppressLint
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.isanechek.extensions.onClick
import com.isanechek.wallpaper.ui.base.BaseRoundedBottomSheetDialog

@SuppressLint("ValidFragment")
class NoWallpaperDialog constructor(private val callback: () -> Unit) : BaseRoundedBottomSheetDialog() {

    private lateinit var behavior: BottomSheetBehavior<View>

    override fun initUI(dialog: BottomSheetDialog) {
        val rootView = LayoutInflater.from(activity).inflate(_layout.no_wallpaper_dialog_layout, null)
        rootView.findViewById<Button>(_id.no_wallpaper_dialog_remove_btn)
                .apply {
                    onClick {
                        callback.invoke()
                        behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
        rootView.findViewById<Button>(_id.no_wallpaper_dialog_cancel_btn)
                .apply {
                    onClick {
                        behavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
        dialog.setContentView(rootView)
        behavior = BottomSheetBehavior.from(rootView.parent as View)
        behavior.skipCollapsed = false

    }

    override fun setupAction() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun setupObserver() {
    }
}