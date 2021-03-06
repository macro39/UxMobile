package sk.uxtweak.uxmobile.study.float_widget

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.devs.vectorchildfinder.VectorChildFinder
import com.devs.vectorchildfinder.VectorDrawableCompat
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.utility.LocalizedContextWrapper
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder
import java.util.*


/**
 * Created by Kamil Macek on 23. 11. 2019.
 */
class FloatWidgetService(
    private val context: Context,
    private val listener: FloatWidgetClickObserver
) {

    private var addedToView = false

    private lateinit var mLayoutInflater: LayoutInflater

    private val mWindowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager


    private lateinit var mFloatView: View

    private lateinit var mDisplaySize: Point

    private var mFloatWidgetMoveController: FloatWidgetMoveController? = null

    private var positionX = 0
    private var positionY = 100

    fun onInit() {
        mLayoutInflater = LayoutInflater.from(LocalizedContextWrapper.wrap(context, Locale(Constants.LANGUAGE)))

        mFloatView = mLayoutInflater.inflate(R.layout.float_widget, null)
    }

    fun onCreate() {
        if (!addedToView) {
            initializeDisplaySize()

            // TODO WindowManager Bad token exe... need to fix
            mWindowManager.addView(mFloatView, getWindowParams())

            setBackgroundColors()

            mFloatWidgetMoveController =
                FloatWidgetMoveController(
                    listener,
                    mWindowManager,
                    mFloatView,
                    mDisplaySize
                )

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

    private fun setBackgroundColors() {
        // set background color of expanded view
        mFloatView.findViewById<LinearLayout>(R.id.expanded_float_widget).background.setColorFilter(
            Color.parseColor(StudyDataHolder.getBackgroundColorPrimary()),
            PorterDuff.Mode.SRC_ATOP
        )

        // set background color of collapsed view
        val vectorLogo =
            VectorChildFinder(context, R.drawable.ic_logo, mFloatView.findViewById(R.id.imageView_float_widget))

        val path1: VectorDrawableCompat.VFullPath = vectorLogo.findPathByName("background")
        path1.fillColor = Color.parseColor(StudyDataHolder.getBackgroundColorPrimary())
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
