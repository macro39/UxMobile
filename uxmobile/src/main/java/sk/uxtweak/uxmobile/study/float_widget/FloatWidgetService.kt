package sk.uxtweak.uxmobile.study.float_widget

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 23. 11. 2019.
 */
class FloatWidgetService(
    private val context: Context,
    private val listener: FloatWidgetClickObserver
) {

    private val mLayoutInflater: LayoutInflater
        get() = LayoutInflater.from(context)

    private val mWindowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


    private lateinit var mFloatView: View

    private lateinit var mDisplaySize: Point

    private lateinit var mFloatWidgetMoveController: FloatWidgetMoveController

    fun onCreate() {
        listener.studyStateChanged(true)
        listener.instructionClicked(false)

        mFloatView = mLayoutInflater.inflate(R.layout.float_widget, null)

        // TODO WindowManager Bad token exe... need to fix
        mWindowManager.addView(mFloatView, getWindowParams())

        initializeDisplaySize()
        mFloatWidgetMoveController =
            FloatWidgetMoveController(context, listener, mWindowManager, mFloatView, mDisplaySize)
    }

    fun onDestroy() {
        if (mFloatView.parent != null) {
            mWindowManager.removeView(mFloatView)
        }
    }

    fun onMinimalize() {

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
