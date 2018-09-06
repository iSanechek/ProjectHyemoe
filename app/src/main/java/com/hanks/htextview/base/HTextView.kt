package com.hanks.htextview.base

import android.content.Context
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatTextView

/*
* Oroginal https://github.com/hanks-zyh/HTextView/blob/master/htextview-base/src/main/java/com/hanks/htextview/base/HTextView.java
*/

abstract class HTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract fun setAnimationListener(listener: AnimationListener)

    abstract fun setProgress(progress: Float)

    abstract fun animateText(text: CharSequence)
}
