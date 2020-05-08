package sk.uxtweak.uxmobile.recorder.events

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.GestureDetector
import android.view.MotionEvent
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.core.withFixedDelay
import sk.uxtweak.uxmobile.lifecycle.*
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.recorder.screen.attachListenerToPopupView
import sk.uxtweak.uxmobile.recorder.screen.popupViews
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

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
    private val throttler = EventThrottler(THROTTLE_DELAY)
    private lateinit var job: Job

    private val eventListeners = mutableListOf<(Event) -> Unit>()

    private val observer = object : LifecycleObserverAdapter() {
        override fun onAnyActivityStarted(activity: Activity) {
            connector.changeActivity(activity)
        }
    }

    private val attachJob: suspend CoroutineScope.() -> Unit = {
        ForegroundActivityHolder.foregroundActivity?.popupViews?.forEach { view ->
            view.view.attachListenerToPopupView {
                gestureDetector.onTouchEvent(it)
            }
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
        connector.changeActivity(ForegroundActivityHolder.foregroundActivity)
        job = GlobalScope.withFixedDelay(Dispatchers.Main, ATTACH_RATE, attachJob)
    }

    fun stop() {
        logd(TAG, "Stopping event recorder")
        isRunning = false
        job.cancel()
        connector.changeActivity(null)
        lifecycle -= observer
        connector -= ::onTouchEvent
        unregisterExceptionHandler()
        activityEventRecorder.stop()
    }

    fun addOnEventListener(eventListener: (Event) -> Unit) {
        eventListeners += eventListener
    }

    fun removeOnEventListener(eventListener: (Event) -> Unit) {
        eventListeners -= eventListener
    }

    private fun onTouchEvent(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    private fun onEvent(event: Event) {
        if (event is Event.ScrollEvent && throttler.throttle()) {
            return
        }
        dispatchEvent(event)
    }

    private fun dispatchEvent(event: Event) {
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

    companion object {
        private const val THROTTLE_DELAY = 75L
        private const val ATTACH_RATE = 50L
    }
}
