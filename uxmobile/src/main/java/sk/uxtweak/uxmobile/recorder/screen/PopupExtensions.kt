package sk.uxtweak.uxmobile.recorder.screen

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.PopupWindow
import sk.uxtweak.uxmobile.recorder.events.WindowCallbackAdapter
import sk.uxtweak.uxmobile.util.getFieldExtended
import sk.uxtweak.uxmobile.util.getFieldInstance

val Activity.popupViews: List<PopupView>
    get() {
        val views = mutableListOf<PopupView>()

        val manager = try {
            windowManager::class.java.getFieldExtended("mWindowManager")
            windowManager.getFieldInstance<Any>("mWindowManager")
        } catch (exception: NoSuchFieldException) {
            windowManager.getFieldInstance<Any>("mGlobal")
        }

        val roots = manager!!.getFieldInstance<List<*>>("mRoots")
        roots!!.forEachIndexed { index, root ->
            val view = root!!.getFieldInstance<View>("mView")
            if (view != null) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val position = Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)
                if (view.isShown) {
                    views += PopupView(view, position)
                }
            }
        }
        return views
    }

fun View.attachListenerToPopupView(callback: (MotionEvent) -> Unit) {
    if (context !is Activity) {
        try {
            val window = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFieldInstance<Window>("mWindow")
            } else {
                getFieldInstance<Window>("this$0")
            }) ?: return
            if (window.callback !is WindowCallbackAdapter) {
                window.callback = object : WindowCallbackAdapter(window.callback) {
                    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                        callback(event)
                        return super.dispatchTouchEvent(event)
                    }
                }
            }
        } catch (exception: Exception) {
            val window = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFieldInstance<PopupWindow>("mWindow")
            } else {
                getFieldInstance<PopupWindow>("this$0")
            }) ?: return
            window.setTouchInterceptor { v, event ->
                callback(event)
                false
            }
        }
    }
}

data class PopupView(
    val view: View,
    val position: Rect
)
