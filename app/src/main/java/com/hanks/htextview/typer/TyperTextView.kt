package com.hanks.htextview.typer

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Message
import android.util.AttributeSet

import com.hanks.htextview.base.AnimationListener
import com.hanks.htextview.base.HTextView
import com.isanechek.wallpaper.R

import java.util.Random

/**
 * Typer Effect
 * Created by hanks on 2017/3/15.
 * Original https://github.com/hanks-zyh/HTextView/blob/master/htextview-typer/src/main/java/com/hanks/htextview/typer/TyperTextView.java
 */

class TyperTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : HTextView(context, attrs, defStyleAttr) {
    private val random: Random
    private var mText: CharSequence
    internal lateinit var handler: Handler
    var charIncrease: Int = 0
    var typerSpeed: Int = 0
    private var animationListener: AnimationListener? = null

    init {
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TyperTextView)
        typerSpeed = typedArray.getInt(R.styleable.TyperTextView_typerSpeed, 100)
        charIncrease = typedArray.getInt(R.styleable.TyperTextView_charIncrease, 2)
        typedArray.recycle()

        random = Random()
        mText = text
        handler = Handler {
            val currentLength = text.length
            if (currentLength < mText.length) {
                if (currentLength + charIncrease > mText.length) {
                    charIncrease = mText.length - currentLength
                }
                append(mText.subSequence(currentLength, currentLength + charIncrease))
                val randomTime = (typerSpeed + random.nextInt(typerSpeed)).toLong()
                val message = Message.obtain()
                message.what = INVALIDATE
                handler.sendMessageDelayed(message, randomTime)
                false
            } else {
                if (animationListener != null) {
                    animationListener!!.onAnimationEnd(this@TyperTextView)
                }
            }
            false
        }
    }

    override fun setAnimationListener(listener: AnimationListener) {
        animationListener = listener
    }

    override fun setProgress(progress: Float) {
        text = mText.subSequence(0, (mText.length * progress).toInt())
    }

    override fun animateText(text: CharSequence) {
        mText = text
        setText("")
        val message = Message.obtain()
        message.what = INVALIDATE
        handler.sendMessage(message)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeMessages(INVALIDATE)
    }
}

private const val INVALIDATE = 0x767