package sk.uxtweak.uxmobile.lifecycle

import android.app.Activity
import java.lang.ref.WeakReference

object ForegroundActivityHolder : LifecycleObserverAdapter() {
    private var foregroundActivityReference: WeakReference<Activity>? = null
    val foregroundActivity: Activity?
        get() = foregroundActivityReference?.get()

    private var onActivityChanged: (Activity) -> Unit = {}

    fun register(lifecycle: Lifecycle) = lifecycle.addObserver(this)

    override fun onAnyActivityStarted(activity: Activity) {
        foregroundActivityReference = WeakReference(activity)
        onActivityChanged(activity)
        onActivityChanged = {}
    }

    override fun onLastActivityStopped(activity: Activity) {
        foregroundActivityReference = null
    }

    fun doOnActivity(action: (Activity) -> Unit) {
        onActivityChanged = action
    }
}

inline fun ForegroundActivityHolder.withForegroundActivity(action: (Activity) -> Unit) {
    foregroundActivity?.let { action(it) }
}
