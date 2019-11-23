package sk.uxtweak.uxmobile.study.float_button

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.view.*
import android.widget.Toast
import sk.uxtweak.uxmobile.R
import kotlin.math.abs

/**
 * Created by Kamil Macek on 23. 11. 2019.
 */
class FloatButtonService(
    private val context: Context
) {

    private val mLayoutInflater: LayoutInflater
        get() = LayoutInflater.from(context)

    private val mWindowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var isOnRightSide = true

    private var initX = 0
    private var initY = 0

    private var marginX = 0
    private var marginY = 0

    private lateinit var mFloatView: View
    private lateinit var bubbleView: View

    private lateinit var mDisplaySize: Point



    fun onCreate() {
        mFloatView = mLayoutInflater.inflate(R.layout.float_button, null)

        // TODO WindowManager Bad token exe... need to fix
        mWindowManager.addView(mFloatView, getWindowParams())

        bubbleView = mFloatView.findViewById(R.id.bubble)

        initializeDisplaySize()
        bindOnTouchListener()
    }

    fun onDestroy() {
        if (mFloatView.parent != null) {
            mWindowManager.removeView(mFloatView)
        }
    }

    private fun bindOnTouchListener() {
        mFloatView.setOnTouchListener(object : View.OnTouchListener {
            var start: Long = 0
            var end: Long = 0

            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {

                var layoutParams = mFloatView.layoutParams as WindowManager.LayoutParams

                var shiftX = motionEvent.rawX.toInt()
                var shiftY = motionEvent.rawY.toInt()

                var destinationX: Int
                var destinationY: Int

                when (motionEvent.action) {

                    MotionEvent.ACTION_DOWN -> {
                        start = System.currentTimeMillis()

                        initX = shiftX
                        initY = shiftY

                        marginX = layoutParams.x
                        marginY = layoutParams.y

                        return true
                    }

                    MotionEvent.ACTION_UP -> {

                        var diffX = shiftX - initX
                        var diffY = shiftY - initY

                        if (abs(diffX) < 10 && abs(diffY) < 10) {
                            end = System.currentTimeMillis()

                            if ((end - start) < 300) {
                                floatButtonClicked()
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

    private fun floatButtonClicked() {
        Toast.makeText(context, "CLICKED", Toast.LENGTH_SHORT).show()
        return
    }

    fun changePosition(currentX: Int) {
        if (currentX > (mDisplaySize.x / 2)) {
            isOnRightSide = false
            moveLeft(currentX)
        } else {
            isOnRightSide = true
            moveRight(currentX)
        }
    }


    fun moveRight(currentX: Int) {
        val timer = object : CountDownTimer(500, 5) {
            var mParams = mFloatView.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.x = mDisplaySize.x + (currentX * currentX * step).toInt() - mFloatView.width
                //mParams.x = mDisplaySize.x + (getMoveValue(step, currentX) - mParams.width).toInt()

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }

            override fun onFinish() {
                mParams.x = mDisplaySize.x
                mWindowManager.updateViewLayout(mFloatView, mParams)
            }
        }

        timer.start()
    }

    fun moveLeft(currentX: Int) {
        var x = mDisplaySize.x - currentX

        val timer = object : CountDownTimer(500, 5) {
            var mParams = mFloatView.layoutParams as WindowManager.LayoutParams

            override fun onTick(millisUntilFinished: Long) {
                var step = (500 - millisUntilFinished) / 5

                mParams.x = 0 - (currentX * currentX * step).toInt()
                //mParams.x = 0 - getMoveValue(step, x).toInt()

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                mWindowManager.updateViewLayout(mFloatView, mParams)
            }
        }
        timer.start()
    }

    private fun initializeDisplaySize() {
        val width = mWindowManager.defaultDisplay.width
        val height = mWindowManager.defaultDisplay.height

        mDisplaySize = Point()

        mDisplaySize.set(width, height)
    }

    private fun getWindowParams(): WindowManager.LayoutParams {
        // android version checker - different layout parameter
        val params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }

        params.gravity = Gravity.TOP or Gravity.RIGHT
        params.x = 0
        params.y = 100

        return params
    }
}
