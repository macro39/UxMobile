package sk.uxtweak.uxmobile.recorder.events

import android.app.Activity
import android.view.MotionEvent

/**
 * Registers window callback to activity and passes events to registered listeners. Uses activity
 * lifecycle to unregister when the activity is destroyed. After activity is destroyed, this object
 * removes all added listeners and should no longer be used.
 */
class WindowCallbackConnector(activity: Activity? = null) {
    private var currentActivity: Activity? = null
    private val touchEventListeners = mutableListOf<(MotionEvent) -> Unit>()

    init {
        onActivityChanged(activity)
    }

    fun onActivityChanged(activity: Activity?) {
        unregisterWindowCallback()
        currentActivity = activity
        registerWindowCallback()
    }

    fun addEventListener(listener: (MotionEvent) -> Unit) {
        touchEventListeners += listener
    }

    fun removeEventListener(listener: (MotionEvent) -> Unit) {
        touchEventListeners -= listener
    }

    operator fun plusAssign(listener: (MotionEvent) -> Unit) = addEventListener(listener)
    operator fun minusAssign(listener: (MotionEvent) -> Unit) = removeEventListener(listener)

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

    private fun unregisterWindowCallback() {
        currentActivity?.window?.callback =
            (currentActivity?.window?.callback as? WindowCallbackAdapter)?.baseCallback
                ?: currentActivity?.window?.callback
    }
}
