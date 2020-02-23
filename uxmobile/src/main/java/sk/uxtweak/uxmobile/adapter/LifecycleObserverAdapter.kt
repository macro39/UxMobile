package sk.uxtweak.uxmobile.adapter

import android.app.Activity
import android.content.res.Configuration
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.Lifecycle

/**
 * Any class that extends this class should be registered to lifecycle by calling [registerObserver].
 */
abstract class LifecycleObserverAdapter : LifecycleObserver {
    override fun onFirstActivityStarted(activity: Activity) {}
    override fun onAnyActivityStarted(activity: Activity) {}
    override fun onAnyActivityStopped(activity: Activity) {}
    override fun onLastActivityStopped(activity: Activity) {}
    override fun onConfigurationChanged(configuration: Configuration) {}

    fun registerObserver(lifecycle: Lifecycle) = lifecycle.addObserver(this)
    fun unregisterObserver(lifecycle: Lifecycle) = lifecycle.removeObserver(this)
}
