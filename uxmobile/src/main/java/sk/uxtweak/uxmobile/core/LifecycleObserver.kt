package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.content.res.Configuration

interface LifecycleObserver {
    fun onFirstActivityStarted(activity: Activity)
    fun onEveryActivityStarted(activity: Activity)
    fun onEveryActivityStopped(activity: Activity)
    fun onLastActivityStopped(activity: Activity)
    fun onConfigurationChanged(configuration: Configuration)
}
