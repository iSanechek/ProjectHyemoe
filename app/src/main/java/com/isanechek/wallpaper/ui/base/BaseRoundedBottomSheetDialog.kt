package com.isanechek.wallpaper.ui.base

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseRoundedBottomSheetDialog : BottomSheetDialogFragment() {

    override fun getTheme(): Int = _style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        initUI(dialog)
        setupObserver()
        return dialog
    }

    override fun onStart() {
        super.onStart()
        setupAction()
    }

    abstract fun initUI(dialog: BottomSheetDialog)
    abstract fun setupAction()
    abstract fun setupObserver()
}