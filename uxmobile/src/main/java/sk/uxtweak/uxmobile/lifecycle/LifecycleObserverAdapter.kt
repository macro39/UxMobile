package sk.uxtweak.uxmobile.lifecycle

import android.app.Activity
import android.content.res.Configuration

/**
 * Any class that extends this class should be registered to lifecycle by calling [registerObserver].
 */
abstract class LifecycleObserverAdapter : LifecycleObserver {
    override fun onFirstActivityStarted(activity: Activity) {}
    override fun onAnyActivityStarted(activity: Activity) {}
    override fun onAnyActivityStopped(activity: Activity) {}
    override fun onLastActivityStopped(activity: Activity) {}
    override fun onConfigurationChanged(configuration: Configuration) {}
}
