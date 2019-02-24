package com.indieteam.mytask.ui

import android.animation.ValueAnimator
import android.graphics.Point
import android.util.Log
import kotlinx.android.synthetic.main.activity_week.*

class CustomSwipe(private val activity: WeekActivity) {

    private var screenW = 0
    private var screenY = 0
    private var time: Long = 130
    private var timeback: Long = 90
    var lastXListview = 0f

    private fun screenSize() {
        activity.apply {
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            screenW = point.x
            screenY = point.y
        }
    }

    var i = 0

    fun left() {
        screenSize()
        i++
        if (i <= 1)
            lastXListview = activity.calender_list_view.x
        val valueAnimation = ValueAnimator.ofFloat(0f, -screenW.toFloat())
        valueAnimation.duration = time
        valueAnimation.addUpdateListener {
            Log.d("lastX", lastXListview.toString())
            Log.d("animatedValue", screenW.toString() + "," + it.animatedValue.toString())
            if ((it.animatedValue as Float) > -screenW.toFloat()) {
                activity.apply {
                    runOnUiThread {
                        calender_list_view.x = it.animatedValue as Float
                    }
                }
            } else {
                activity.apply {
                    runOnUiThread {
                        nextDate()
                        leftBack()
                    }
                }
                it.cancel()
            }
        }
        valueAnimation.start()
    }

    fun rightBack() {
        screenSize()
        val valueAnimation = ValueAnimator.ofFloat(screenW.toFloat(), 0f)
        valueAnimation.duration = timeback
        Log.d("lastX", lastXListview.toString())
        valueAnimation.addUpdateListener {
            Log.d("animatedValue", screenW.toString() + "," + it.animatedValue.toString())
            if ((it.animatedValue as Float) > 0f) {
                activity.apply {
                    runOnUiThread {
                        calender_list_view.x = -(it.animatedValue as Float)
                    }
                }
            } else {
                activity.apply {
                    runOnUiThread {
                        calender_list_view.x = lastXListview
                    }
                }
                it.cancel()
            }
        }
        valueAnimation.start()
    }

    fun leftBack() {
        screenSize()
        val valueAnimation = ValueAnimator.ofFloat(screenW.toFloat(), 0f)
        valueAnimation.duration = timeback
        valueAnimation.addUpdateListener {
            Log.d("animatedValue", screenW.toString() + "," + it.animatedValue.toString())
            if ((it.animatedValue as Float) > 0f) {
                activity.apply {
                    runOnUiThread {
                        calender_list_view.x = it.animatedValue as Float
                    }
                }
            } else {
                activity.apply {
                    runOnUiThread {
                        calender_list_view.x = lastXListview
                    }
                }
                it.cancel()
            }
        }
        valueAnimation.start()
    }

    fun right() {
        screenSize()
        i++
        if (i <= 1)
            lastXListview = activity.calender_list_view.x
        val valueAnimation = ValueAnimator.ofFloat(screenW.toFloat(), 0f)
        valueAnimation.duration = time
        valueAnimation.addUpdateListener {
            Log.d("animatedValue", screenW.toString() + "," + it.animatedValue.toString())
            if ((it.animatedValue as Float) > 0f) {
                activity.apply {
                    runOnUiThread {
                        calender_list_view.x = screenW - it.animatedValue as Float
                    }
                }
            } else {
                activity.apply {
                    runOnUiThread {
                        preDate()
                        rightBack()
                    }
                }
                it.cancel()
            }
        }
        valueAnimation.start()
    }
}