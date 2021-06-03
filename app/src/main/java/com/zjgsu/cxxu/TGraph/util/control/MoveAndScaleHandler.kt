package com.zjgsu.cxxu.TGraph.util.control

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import com.nineoldandroids.view.ViewHelper
import com.zjgsu.cxxu.TGraph.Lg
import kotlin.math.abs
import kotlin.math.sqrt

/**
 *
 */
class MoveAndScaleHandler(
    private val view: View
) : OnScaleGestureListener {
    companion object {
        private const val TAG = "MoveAndScaleHandler"
        const val max_scale = 1.2f
        const val min_scale = 0.5f
    }

    private var lastX = 0
    private var lastY = 0
    private var mode = 0
    fun onTouchEvent(event: MotionEvent): Boolean {
        Lg.d(TAG, "get touch Event")
        /**android.view.MotionEvent
         * public float getRawX()
         * Returns the original raw X coordinate of this event.
         * For touch events on the screen, this is the original location of the event on the screen,
         * before it had been adjusted for the containing window and views.*/
        val currentX = event.rawX.toInt() //获得手指当前的坐标,相对于屏幕
        val currentY = event.rawY.toInt()
        Lg.d(TAG, "judging the Action...")
        when (event.action and MotionEvent.ACTION_MASK) {
//            Lg.d(TAG,"judging the Action...")//you can't use the Lg in the when structure:but you can use it above
            MotionEvent.ACTION_DOWN -> mode = 1
            MotionEvent.ACTION_UP -> mode = 0
            //将模式指定为负数这样，多指下，抬起不会触发移动
            MotionEvent.ACTION_POINTER_UP -> mode = -2
            MotionEvent.ACTION_POINTER_DOWN -> mode += 1
            /*special judging for the _MOVE:
            * 在按下手势期间发生了变化(在ACTION DOWN和ACTION UP之间)。
            * 运动包含最近的点，以及自最后一个向下或移动事件以来的任何中间点。*/
            MotionEvent.ACTION_MOVE -> if (mode >= 2) {
                //缩放
                //mScaleGestureDetector.onTouchEvent(event);
            } else if (mode == 1) {
                val deltaX = currentX - lastX
                val deltaY = currentY - lastY
                val translationX = ViewHelper.getTranslationX(view).toInt() + deltaX
                val translationY = ViewHelper.getTranslationY(view).toInt() + deltaY
                ViewHelper.setTranslationX(view, translationX.toFloat())
                ViewHelper.setTranslationY(view, translationY.toFloat())
            }
        }
        lastX = currentX
        lastY = currentY
        return true
    }

    /**
     * 两点之间的距离
     *
     * @param event
     * @return
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * 重写
     * ScaleGestureDetector.OnScaleGestureListener (内部接口中的方法(默认抽象))
     *
     *
     * Responds to scaling events for a gesture in progress. Reported by pointer motion.
     */
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var scaleFactor = detector.scaleFactor
        /*处理过度放大/缩小*/if (scaleFactor >= max_scale) {
            scaleFactor = max_scale
        }
        if (scaleFactor <= min_scale) {
            scaleFactor = min_scale
        }
        /**
         * android.view.ItemView
         * public float getScaleX()
         * The amount that the view is scaled in x around the pivot point,
         * as a proportion(比例) of the view's unscaled未缩放的 width.
         * A value of 1, the default, means that no scaling is applied.
         * By default, this is 1.0f. */
        val old = view.scaleX
        if (abs(scaleFactor - old) > 0.6 || abs(scaleFactor - old) < 0.02) {
            //忽略
        } else {
            /*利用动画库nineoldandroids提供的ViewHelper*/
            ViewHelper.setScaleX(view, scaleFactor)
            ViewHelper.setScaleY(view, scaleFactor)
        }
        return false
    }

    /**
     * ScaleGestureDetector.OnScaleGestureListener
     * Responds to the beginning of a scaling gesture. Reported by new pointers going down.
     * @param detector
     * @return
     */
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    /**
     * Description copied from interface:
     * ScaleGestureDetector.OnScaleGestureListener Responds to the end of a scale gesture. Reported by existing pointers going up. Once a scale has ended, getFocusX() and getFocusY() will return focal point of the pointers remaining on the screen.
     * Specified by:
     * onScaleEnd in interface OnScaleGestureListener
     * Params:
     * detector – The detector reporting the event - use this to retrieve extended info about event state. */
    override fun onScaleEnd(detector: ScaleGestureDetector) {}


}