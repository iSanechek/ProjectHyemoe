package com.isanechek.wallpaper.ui.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.util.AttributeSet


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class SharedTransitionSet : TransitionSet {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor() : super() {
        init()
    }

    private fun init() {
        ordering = TransitionSet.ORDERING_TOGETHER
        addTransition(ChangeBounds()).addTransition(ChangeTransform()).addTransition(ChangeImageTransform())
    }
}