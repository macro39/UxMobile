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

    private var addedToView = false

    private val mLayoutInflater: LayoutInflater
        get() = LayoutInflater.from(context)

    private val mWindowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


    private lateinit var mFloatView: View

    private lateinit var mDisplaySize: Point

    private var mFloatWidgetMoveController: FloatWidgetMoveController? = null

    private var positionX = 0
    private var positionY = 100

    fun onInit() {
        mFloatView = mLayoutInflater.inflate(R.layout.float_widget, null)
    }

    fun onCreate() {
        if (!addedToView) {
            initializeDisplaySize()

            // TODO WindowManager Bad token exe... need to fix
            mWindowManager.addView(mFloatView, getWindowParams())

            mFloatWidgetMoveController =
                FloatWidgetMoveController(context, listener, mWindowManager, mFloatView, mDisplaySize)

            addedToView = true
        }
    }

    fun onDestroy() {
        if (!addedToView) {
            return
        }
        if (mFloatWidgetMoveController != null) {
            val positionParams = mFloatWidgetMoveController?.getPosition()

            // save last x and y position of float widget
            positionX = positionParams!!.x
            positionY = positionParams.y

            if (mFloatView.parent != null) {
                mWindowManager.removeView(mFloatView)
                addedToView = false
            }
        }
    }

    fun setVisibility(visible: Boolean) {
        if (visible) {
            mFloatView.visibility = View.VISIBLE
        } else {
            mFloatView.visibility = View.GONE

        }
    }

    fun changeFloatButtonState(expandView: Boolean) {
        mFloatWidgetMoveController?.changeFloatButtonState(expandView)
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

        params.gravity = Gravity.TOP or Gravity.END
        params.x = positionX
        params.y = positionY

        return params
    }
}
