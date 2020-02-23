package sk.uxtweak.uxmobile.adapter

import android.app.Activity
import android.content.Context
import android.view.MotionEvent

/**
 * Registers window callback to activity and passes events to registered listeners. Uses activity
 * lifecycle to unregister when the activity is destroyed. After activity is destroyed, this object
 * removes all added listeners and should no longer be used.
 */
class WindowCallbackConnector(activity: Activity? = null) {
    private val touchEventListeners = mutableListOf<(MotionEvent) -> Unit>()

    var currentActivity: Activity? = activity
        set(value) {
            unregisterWindowCallback()
            field = value
            registerWindowCallback()
        }

    val context: Context
        get() = currentActivity!!

    fun addListener(listener: (MotionEvent) -> Unit) {
        touchEventListeners += listener
    }

    fun removeListener(listener: (MotionEvent) -> Unit) {
        touchEventListeners -= listener
    }

    operator fun plusAssign(listener: (MotionEvent) -> Unit) = addListener(listener)
    operator fun minusAssign(listener: (MotionEvent) -> Unit) = removeListener(listener)

    private fun destroy() {
        unregisterWindowCallback()
        currentActivity = null
    }

    private fun registerWindowCallback() {
        val previousCallback = currentActivity?.window?.callback
        if (previousCallback is WindowCallbackAdapter) {
            return
        }

        currentActivity?.window?.callback =
            object : WindowCallbackAdapter(currentActivity?.window?.callback) {
                override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                    touchEventListeners.forEach { it(event) }
                    return super.dispatchTouchEvent(event)
                }
            }
    }

    // TODO: Refactor to something more readable
    private fun unregisterWindowCallback() {
        currentActivity?.window?.callback =
            (currentActivity?.window?.callback as? WindowCallbackAdapter)?.baseCallback
                ?: currentActivity?.window?.callback
    }
}
