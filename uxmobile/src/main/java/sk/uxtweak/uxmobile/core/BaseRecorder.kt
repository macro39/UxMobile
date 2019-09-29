package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.content.res.Configuration

abstract class BaseRecorder : LifecycleCallback {
    protected var currentActivity: Activity? = null

    override fun onFirstActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onEveryActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onEveryActivityStopped(activity: Activity) {}
    override fun onLastActivityStopped(activity: Activity) {}
    override fun onConfigurationChanged(configuration: Configuration) {}

    open fun onSessionStarted() {}
}
