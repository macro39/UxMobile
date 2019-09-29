package sk.uxtweak.uxmobile.adapter

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log

import sk.uxtweak.uxmobile.core.LifecycleCallback

class LifecycleObserver(
    private val callback: LifecycleCallback = EmptyLifecycleCallback()
) : Application.ActivityLifecycleCallbacks, ComponentCallbacks {
    private var resumed = false

    private var activityCounter = 0
    private var anyActivityStarted = false
    private var pausedWithConfig = false

    private var latestConfiguration: Configuration? = null

    private val isFirstActivity: Boolean
        get() = activityCounter == 1
    private val isLastActivity: Boolean
        get() = activityCounter == 0

    override fun onActivityStarted(activity: Activity) {
        Log.d("UxMobile", "onActivityStarted: " + activity.localClassName)
        latestConfiguration = null

        activityCounter++

        if (isFirstActivity && !anyActivityStarted) {
            callback.onFirstActivityStarted(activity)
            anyActivityStarted = true
        }

        callback.onEveryActivityStarted(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d("UxMobile", "onActivityStopped: " + activity.localClassName)

        activityCounter--

        callback.onEveryActivityStarted(activity)

        if (isLastActivity && !pausedWithConfig) {
            callback.onLastActivityStopped(activity)

            latestConfiguration = null
            anyActivityStarted = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        latestConfiguration = newConfig
        if (resumed) {
            callback.onConfigurationChanged(newConfig)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumed = false
        pausedWithConfig = latestConfiguration != null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityResumed(activity: Activity) {
        resumed = true
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onLowMemory() {}
}
