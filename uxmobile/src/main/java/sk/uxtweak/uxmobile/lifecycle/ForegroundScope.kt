package sk.uxtweak.uxmobile.lifecycle

import android.app.Activity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

object ForegroundScope : LifecycleObserverAdapter(), CoroutineScope {
    private var reusableContext: CoroutineContext = SupervisorJob() + Dispatchers.Main

    override val coroutineContext: CoroutineContext
        get() = reusableContext

    private val isCancelled: Boolean
        get() = coroutineContext[Job]?.isCancelled ?: false

    init {
        ApplicationLifecycle.setForegroundScopeObserver(this)
    }

    override fun onFirstActivityStarted(activity: Activity) {
        if (isCancelled) {
            reusableContext = SupervisorJob() + Dispatchers.Main
        }
    }

    override fun onLastActivityStopped(activity: Activity) = cancel()
}
