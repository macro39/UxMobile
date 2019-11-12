package sk.uxtweak.uxmobile.lifecycle

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter

abstract class SessionCoroutineScope : LifecycleObserverAdapter(), CoroutineScope by MainScope() {
    init {
        ApplicationLifecycle.addObserver(this)
    }

    override fun onLastActivityStopped(activity: Activity) {
        ApplicationLifecycle.removeObserver(this)
        cancel()
    }
}
