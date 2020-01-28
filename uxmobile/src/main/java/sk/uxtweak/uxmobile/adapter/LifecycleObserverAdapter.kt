package sk.uxtweak.uxmobile.adapter

import android.app.Activity
import android.content.res.Configuration
import sk.uxtweak.uxmobile.core.LifecycleObserver

abstract class LifecycleObserverAdapter : LifecycleObserver {
    override fun onFirstActivityStarted(activity: Activity) {}
    override fun onAnyActivityStarted(activity: Activity) {}
    override fun onAnyActivityStopped(activity: Activity) {}
    override fun onLastActivityStopped(activity: Activity) {}
    override fun onConfigurationChanged(configuration: Configuration) {}
}
