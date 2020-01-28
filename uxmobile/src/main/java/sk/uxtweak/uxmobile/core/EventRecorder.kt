package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.content.res.Configuration
import android.view.GestureDetector
import android.view.MotionEvent
import sk.uxtweak.uxmobile.ExceptionHandler
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.adapter.WindowCallbackAdapter
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.model.events.Event

typealias EventListener = (Event) -> Unit

class EventRecorder : LifecycleObserverAdapter() {
    private var orientation = 0
    private var configurationRecentlyChanged = false

    private val motionEventConverter = MotionEventConverter(::onMotionEvent)
    private lateinit var gestureDetector: GestureDetector

    private val eventListeners = mutableListOf<EventListener>()

    init {
        ApplicationLifecycle.addObserver(this)
        registerExceptionHandler()
    }

    fun addOnEventListener(eventListener: EventListener) {
        eventListeners += eventListener
    }

    override fun onFirstActivityStarted(activity: Activity) {
        dispatchEvent(Event.SessionStartEvent)
        orientation = activity.resources.configuration.orientation
        gestureDetector = GestureDetector(activity.applicationContext, motionEventConverter)
    }

    override fun onLastActivityStopped(activity: Activity) {
        dispatchEvent(Event.SessionEndEvent)
    }

    override fun onAnyActivityStarted(activity: Activity) {
        handleConfigurationChange(activity)
        updateActivityCallback(activity)
    }

    private fun handleConfigurationChange(activity: Activity) {
        if (!configurationRecentlyChanged) {
            dispatchEvent(Event.ActivityStartedEvent(activity.localClassName))
        }
        configurationRecentlyChanged = false
    }

    private fun updateActivityCallback(activity: Activity) {
        val previousCallback = activity.window.callback
        if (previousCallback is WindowCallbackAdapter) {
            return
        }

        activity.window.callback = object : WindowCallbackAdapter(previousCallback) {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(event)
                return super.dispatchTouchEvent(event)
            }
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        configurationRecentlyChanged = true
        if (orientation != configuration.orientation) {
            orientation = configuration.orientation
            dispatchEvent(Event.OrientationEvent(orientation))
        }
    }

    private fun onMotionEvent(event: Event) = dispatchEvent(event)

    private fun dispatchEvent(event: Event) = notifyListeners(event)

    private fun registerExceptionHandler() {
        ExceptionHandler.register()
        ExceptionHandler.setHandlerListener { _, throwable ->
            dispatchEvent(Event.ExceptionEvent(throwable))
        }
    }

    private fun notifyListeners(event: Event) = eventListeners.forEach { it(event) }

    companion object {
        private const val TAG = "UxMobile"
    }
}
