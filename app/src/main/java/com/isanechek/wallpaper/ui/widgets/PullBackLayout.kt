package com.isanechek.wallpaper.ui.widgets

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 XiNGRZ <xxx@oxo.ooo>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.IntDef
import androidx.core.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class PullBackLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val dragger: androidx.customview.widget.ViewDragHelper
    private val minimumFlingVelocity: Int

    /**
     * @return Allowed pulling direction
     */
    /**
     * Sets pulling directions allowed
     *
     * @param direction Directions allowed
     * @see .DIRECTION_UP
     *
     * @see .DIRECTION_DOWN
     */
    @Direction
    @get:Direction
    var direction = DIRECTION_UP or DIRECTION_DOWN

    private var callback: Callback? = null

    init {
        dragger = androidx.customview.widget.ViewDragHelper.create(this, 1f / 8f, ViewDragCallback())
        minimumFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = try {
        dragger.shouldInterceptTouchEvent(ev)
    } catch (e: Exception) {
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = try {
        dragger.processTouchEvent(event)
        true
    } catch (e: Exception) {
        false
    }

    override fun computeScroll() {
        if (dragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun onPullStart() {
        if (callback != null) {
            callback!!.onPullStart()
        }
    }

    private fun onPull(@Direction direction: Int, progress: Float) {
        if (callback != null) {
            callback!!.onPull(direction, progress)
        }
    }

    private fun onPullCancel(@Direction direction: Int) {
        if (callback != null) {
            callback!!.onPullCancel(direction)
        }
    }

    private fun onPullComplete(@Direction direction: Int) {
        if (callback != null) {
            callback!!.onPullComplete(direction)
        }
    }

    private fun reset() {
        dragger.settleCapturedViewAt(0, 0)
        invalidate()
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = intArrayOf(
            DIRECTION_UP,
            DIRECTION_DOWN
    ), flag = true)
    annotation class Direction

    interface Callback {
        fun onPullStart()
        fun onPull(@Direction direction: Int, progress: Float)
        fun onPullCancel(@Direction direction: Int)
        fun onPullComplete(@Direction direction: Int)
    }

    private inner class ViewDragCallback : androidx.customview.widget.ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean = true

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = 0

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int =
                when {
                    direction and (DIRECTION_UP or DIRECTION_DOWN) != 0 -> top
                    direction and DIRECTION_UP != 0 -> Math.min(0, top)
                    direction and DIRECTION_DOWN != 0 -> Math.max(0, top)
                    else -> 0
                }

        override fun getViewHorizontalDragRange(child: View): Int = 0

        override fun getViewVerticalDragRange(child: View): Int = when {
            direction == 0 -> 0
            direction and (DIRECTION_UP or DIRECTION_DOWN) != 0 -> height * 2
            else -> height
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            onPullStart()
        }

        override fun onViewPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int
        ) {
            when {
                top > 0 -> onPull(DIRECTION_DOWN, top.toFloat() / height.toFloat())
                top < 0 -> onPull(DIRECTION_UP, (-top).toFloat() / height.toFloat())
            }
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val top = releasedChild.top
            val slop = if (Math.abs(yvel) > minimumFlingVelocity) height / 6 else height / 3
            when {
                top > 0 -> if (top > slop) {
                    onPullComplete(DIRECTION_DOWN)
                } else {
                    onPullCancel(DIRECTION_DOWN)
                    reset()
                }
                top < 0 -> if (top < -slop) {
                    onPullComplete(DIRECTION_UP)
                } else {
                    onPullCancel(DIRECTION_UP)
                    reset()
                }
            }
        }
    }

    companion object {

        /**
         * Flag indicated pulling up is allowed
         *
         * @see .setDirection
         */
        val DIRECTION_UP = 1

        /**
         * Flag indicated pulling down is allowed
         *
         * @see .setDirection
         */
        val DIRECTION_DOWN = 1 shl 1
    }
}