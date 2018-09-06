package com.isanechek.wallpaper.debug

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.isanechek.common.DebugUtils
import com.isanechek.repository.Request
import org.koin.android.ext.android.inject

private const val TAG = "DebugActivity"

class DebugActivity : Activity() {

    private val debug by inject<DebugUtils>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this)
        val match = ViewGroup.LayoutParams.MATCH_PARENT
        setContentView(container, ViewGroup.LayoutParams(match, match))

        val info = TextView(this)
        info.gravity = Gravity.CENTER
        container.addView(info)

        debug.log("$TAG Hi")

        val request = Request(key = "9ITDMkRBwNFWGfDcwezt7Ca7wrnQvdIX+H/YgWrBCSg=", path = "/wallpapers")





    }
}