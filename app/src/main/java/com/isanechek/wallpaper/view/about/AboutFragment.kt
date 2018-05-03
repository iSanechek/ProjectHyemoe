package com.isanechek.wallpaper.view.about

import com.isanechek.wallpaper.R
import com.isanechek.wallpaper.utils._layout
import com.isanechek.wallpaper.view.base.BaseFragment

/**
 * Created by isanechek on 4/23/18.
 */
class AboutFragment : BaseFragment() {
    override fun layoutResId(): Int = _layout.about_screen_layout

    override fun getTitle(): String = getString(R.string.about_screen_title)
}