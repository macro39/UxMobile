package sk.uxtweak.uxmobile.study.float_widget

import android.graphics.Point
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.math.abs

/**
 * Created by Kamil Macek on 24. 11. 2019.
 */
class FloatWidgetMoveController(
    listener: FloatWidgetClickObserver,
    private val mWindowManager: WindowManager,
    private val mFloatView: View,
    private val mDisplaySize: Point
) {

    private var floatWidgetClickListener: FloatWidgetClickListener =
        FloatWidgetClickListener(mFloatView, listener)

    private var isOnRightSide = true

    private var initX = 0
    private var initY = 0

    private var marginX = 0
    private var marginY = 0

    init {
        bindOnTouchListener()
    }

    fun getPosition(): Point {
        val layoutParams = mFloatView.layoutParams as WindowManager.LayoutParams
        return Point(layoutParams.x, layoutParams.y)
    }

    private fun bindOnTouchListener() {
        mFloatView.setOnTouchListener(object : View.OnTouchListener {
            var start: Long = 0
            var end: Long = 0

            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {

                val layoutParams = mFloatView.layoutParams as WindowManager.LayoutParams

                val shiftX = motionEvent.rawX.toInt()
                val shiftY = motionEvent.rawY.toInt()

                var destinationX: Int
                var destinationY: Int

                when (motionEvent.action) {

                    MotionEvent.ACTION_DOWN -> {

                        if (!floatWidgetClickListener.canMove()) {
                            return false
                        }

                        start = System.currentTimeMillis()

                        initX = shiftX
                        initY = shiftY

                        marginX = layoutParams.x
                        marginY = layoutParams.y

                        return true
                    }

                    MotionEvent.ACTION_UP -> {

                        if (!floatWidgetClickListener.canMove()) {
                            return false
                        }

                        val diffX = shiftX - initX
                        val diffY = shiftY - initY

                        if (abs(diffX) < 10 && abs(diffY) < 10) {
                            end = System.currentTimeMillis()

                            if ((end - start) < 300) {
                                floatWidgetClickListener.onClick(isOnRightSide)
                            }
                        }

                        destinationY = marginY + diffY

                        if (destinationY < 0) {
                            destinationY = 0
                        }

                        if ((destinationY + mFloatView.height) > mDisplaySize.y) {
                            destinationY = mDisplaySize.y - mFloatView.height
                        }

                        layoutParams.y = destinationY

                        changePosition(shiftX)

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {

                        if (!floatWidgetClickListener.canMove()) {
                            return false
                        }

                        layoutParams.x = shiftX + marginX - initX
                        layoutParams.y = shiftY + marginY - initY

                        if (isOnRightSide) {
                            layoutParams.x = mDisplaySize.x - shiftX
                        } else {
                            layoutParams.x = mDisplaySize.x - shiftX - mFloatView.width
                        }


                        layoutParams.y = shiftY - mFloatView.height


//                        if (shiftX > (mDisplaySize.x / 2)) {
//                            layoutParams.x = 0
//                        } else {
//                            layoutParams.x = mDisplaySize.x - mFloatView.width
//                        }
//
//                        layoutParams.y = shiftY - mFloatView.width

                        mWindowManager.updateViewLayout(mFloatView, layoutParams)

                        return true
                    }

                    else -> {
                        return false
                    }
                }
            }
        })
    }

    fun changeFloatButtonState(expandView: Boolean) {
        if (expandView) {
            floatWidgetClickListener.makeViewExpanded(isOnRightSide)
        } else {
            floatWidgetClickListener.makeViewCollapsed(isOnRightSide)
        }
    }

    fun changePosition(currentX: Int) {
        if (currentX > (mDisplaySize.x / 2)) {
            isOnRightSide = true
            moveRight(currentX)
        } else {
            isOnRightSide = false
            moveLeft(currentX)
        }
    }


    private fun moveLeft(currentX: Int) {
        val timer = object : CountDownTimer(500, 5) {
            var mParams = mFloatView.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                val step = (500 - millisUntilFinished) / 5

                mParams.x = mDisplaySize.x + (currentX * currentX * step).toInt() - mFloatView.width

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }

            override fun onFinish() {
                mParams.x = mDisplaySize.x
                mWindowManager.updateViewLayout(mFloatView, mParams)
            }
        }

        timer.start()
    }

    private fun moveRight(currentX: Int) {
        val timer = object : CountDownTimer(500, 5) {
            var mParams = mFloatView.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                val step = (500 - millisUntilFinished) / 5

                mParams.x = 0 - (currentX * currentX * step).toInt()

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }
        }
        timer.start()
    }
}
