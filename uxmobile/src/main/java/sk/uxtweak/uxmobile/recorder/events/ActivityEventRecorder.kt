package sk.uxtweak.uxmobile.recorder.events

import android.app.Activity
import android.content.res.Configuration
import sk.uxtweak.uxmobile.lifecycle.Lifecycle
import sk.uxtweak.uxmobile.lifecycle.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.lifecycle.minusAssign
import sk.uxtweak.uxmobile.lifecycle.plusAssign

class ActivityEventRecorder(private val lifecycle: Lifecycle) {
    interface EventListener {
        fun onActivityStarted(activity: Activity)
        fun onConfigurationChanged(configuration: Configuration)
    }

    private var orientation = 0
    private var configurationRecentlyChanged = false

    private val eventListeners = mutableListOf<EventListener>()

    private val observer = object : LifecycleObserverAdapter() {
        override fun onFirstActivityStarted(activity: Activity) {
            orientation = activity.resources.configuration.orientation
        }

        override fun onAnyActivityStarted(activity: Activity) {
            if (!configurationRecentlyChanged) {
                eventListeners.forEach { it.onActivityStarted(activity) }
            }
            configurationRecentlyChanged = false
        }

        override fun onConfigurationChanged(configuration: Configuration) {
            configurationRecentlyChanged = true
            if (orientation != configuration.orientation) {
                orientation = configuration.orientation
                eventListeners.forEach { it.onConfigurationChanged(configuration) }
            }
        }
    }

    fun start() {
        lifecycle += observer
    }

    fun stop() {
        lifecycle -= observer
    }

    fun addActivityEventListener(listener: EventListener) {
        eventListeners += listener
    }
}

inline fun ActivityEventRecorder.addActivityEventListener(
    crossinline activityStarted: (Activity) -> Unit = {},
    crossinline configurationChanged: (Configuration) -> Unit = {}
) {
    addActivityEventListener(object : ActivityEventRecorder.EventListener {
        override fun onActivityStarted(activity: Activity) = activityStarted(activity)
        override fun onConfigurationChanged(configuration: Configuration) =
            configurationChanged(configuration)
    })
}
