package com.isanechek.wallpaper.view.navigation

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.util.AttributeSet


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
internal class SharedTransitionSet : TransitionSet {

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