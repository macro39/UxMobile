package sk.uxtweak.uxmobile

import android.app.Activity
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import kotlin.coroutines.CoroutineContext

object ForegroundScope : LifecycleObserverAdapter(), CoroutineScope {
    private var reusableContext: CoroutineContext = SupervisorJob() + Dispatchers.Main

    override val coroutineContext: CoroutineContext
        get() = reusableContext

    private val isCancelled: Boolean
        get() = coroutineContext[Job]?.isCancelled ?: false

    init {
        ApplicationLifecycle.addObserver(this)
    }

    override fun onFirstActivityStarted(activity: Activity) {
        if (isCancelled) {
            reusableContext = SupervisorJob() + Dispatchers.Main
        }
    }

    override fun onLastActivityStopped(activity: Activity) = cancel()
}
