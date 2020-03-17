package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.GestureDetector
import android.view.MotionEvent
import sk.uxtweak.uxmobile.ExceptionHandler
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.adapter.WindowCallbackConnector
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.model.events.Event
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver

typealias EventListener = (Event) -> Unit

class EventRecorder(
    context: Context,
    private val floatWidgetClickObserver: FloatWidgetClickObserver
) : LifecycleObserverAdapter() {
    private var orientation = 0
    private var configurationRecentlyChanged = false

    private val motionEventConverter = MotionEventConverter(::onEvent)
    private val gestureDetector = GestureDetector(context, motionEventConverter)
    private val connector = WindowCallbackConnector()

    private val eventListeners = mutableListOf<EventListener>()

    init {
        registerExceptionHandler()
        connector += ::onTouchEvent
    }

    fun addOnEventListener(eventListener: EventListener) {
        eventListeners += eventListener
    }

    override fun onFirstActivityStarted(activity: Activity) {
        orientation = activity.resources.configuration.orientation
    }

    override fun onAnyActivityStarted(activity: Activity) {
        handleConfigurationChange(activity)
        connector.currentActivity = activity
    }

    private fun handleConfigurationChange(activity: Activity) {
        if (!configurationRecentlyChanged) {
            dispatchEvent(Event.ActivityStartedEvent(activity.localClassName))
        }
        configurationRecentlyChanged = false
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        configurationRecentlyChanged = true
        if (orientation != configuration.orientation) {
            orientation = configuration.orientation
            dispatchEvent(Event.OrientationEvent(orientation))
        }
    }

    private fun onTouchEvent(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)
    }

    private fun onEvent(event: Event) {
        when (event) {
            is Event.TapEvent, is Event.LongPressEvent -> floatWidgetClickObserver.onClick()
        }
        dispatchEvent(event)
    }

    private fun dispatchEvent(event: Event) = notifyListeners(event)

    private fun registerExceptionHandler() {
        ExceptionHandler.register()
        ExceptionHandler.setHandlerListener { _, throwable ->
            dispatchEvent(Event.ExceptionEvent(throwable))
        }
    }

    private fun notifyListeners(event: Event) = eventListeners.forEach { it(event) }
}
