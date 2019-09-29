package sk.uxtweak.uxmobile.adapter

import android.app.Activity
import android.content.res.Configuration
import sk.uxtweak.uxmobile.core.LifecycleCallback

class EmptyLifecycleCallback : LifecycleCallback {
    override fun onFirstActivityStarted(activity: Activity) {}
    override fun onEveryActivityStarted(activity: Activity) {}
    override fun onEveryActivityStopped(activity: Activity) {}
    override fun onLastActivityStopped(activity: Activity) {}
    override fun onConfigurationChanged(configuration: Configuration) {}
}
