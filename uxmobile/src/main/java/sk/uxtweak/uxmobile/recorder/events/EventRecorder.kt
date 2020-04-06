package sk.uxtweak.uxmobile.recorder.events

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.GestureDetector
import android.view.MotionEvent
import sk.uxtweak.uxmobile.lifecycle.*
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

private typealias EventListener = (Event) -> Unit

class EventRecorder(
    context: Context,
    private val lifecycle: Lifecycle
) {
    var isRunning: Boolean = false
        private set

    private val activityEventRecorder = ActivityEventRecorder(lifecycle)
    private val motionEventConverter = MotionEventConverter(::onEvent)
    private val gestureDetector = GestureDetector(context, motionEventConverter)
    private val connector = WindowCallbackConnector()

    private val eventListeners = mutableListOf<EventListener>()

    private val observer = object : LifecycleObserverAdapter() {
        override fun onAnyActivityStarted(activity: Activity) {
            connector.onActivityChanged(activity)
        }
    }

    init {
        activityEventRecorder.addActivityEventListener(
            ::onActivityStarted,
            ::onConfigurationChanged
        )
    }

    fun start() {
        logd(TAG, "Starting event recorder")
        isRunning = true
        activityEventRecorder.start()
        registerExceptionHandler()
        connector += ::onTouchEvent
        lifecycle += observer
        connector.onActivityChanged(ForegroundActivityHolder.foregroundActivity)
    }

    fun stop() {
        logd(TAG, "Stopping event recorder")
        isRunning = false
        connector.onActivityChanged(null)
        lifecycle -= observer
        connector -= ::onTouchEvent
        unregisterExceptionHandler()
        activityEventRecorder.stop()
    }

    fun addOnEventListener(eventListener: EventListener) {
        eventListeners += eventListener
    }

    fun removeOnEventListener(eventListener: EventListener) {
        eventListeners -= eventListener
    }

    private fun onTouchEvent(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    private fun onEvent(event: Event) {
        dispatchEvent(event)
    }

    private fun dispatchEvent(event: Event) {
        logd(TAG, "Dispatching event")
        eventListeners.forEach { it(event) }
    }

    private fun registerExceptionHandler() {
        ExceptionHandler.register()
        ExceptionHandler.setHandlerListener { _, throwable ->
            dispatchEvent(Event.ExceptionEvent(throwable))
        }
    }

    private fun unregisterExceptionHandler() = ExceptionHandler.unregister()

    private fun onActivityStarted(activity: Activity) =
        dispatchEvent(Event.ActivityStartedEvent(activity.localClassName))

    private fun onConfigurationChanged(configuration: Configuration) =
        dispatchEvent(Event.OrientationEvent(configuration.orientation))
}
