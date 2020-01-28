package sk.uxtweak.uxmobile.lifecycle

import android.app.Activity
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import java.lang.ref.WeakReference

object ForegroundActivityHolder : LifecycleObserverAdapter() {
    private var foregroundActivityReference: WeakReference<Activity>? = null
    val foregroundActivity: Activity?
        get() = foregroundActivityReference?.get()

    fun register() {
        ApplicationLifecycle.addObserver(this)
    }

    override fun onAnyActivityStarted(activity: Activity) {
        foregroundActivityReference = WeakReference(activity)
    }

    override fun onLastActivityStopped(activity: Activity) {
        foregroundActivityReference = null
    }
}

inline fun ForegroundActivityHolder.withForegroundActivity(action: (Activity) -> Unit) {
    val activity = foregroundActivity
    if (activity != null) {
        action(activity)
    }
}
