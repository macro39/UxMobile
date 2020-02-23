package sk.uxtweak.uxmobile.lifecycle

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import java.util.concurrent.CopyOnWriteArrayList

object ApplicationLifecycle : Lifecycle, Application.ActivityLifecycleCallbacks,
    ComponentCallbacks2 {
    private const val TAG = "UxMobile"

    private val observers = CopyOnWriteArrayList<LifecycleObserver>()
    private var foregroundScopeObserver: LifecycleObserver? = null

    private var resumed = false
    private var activityCounter = 0

    private var anyActivityStarted = false
    private var pausedWithConfig = false
    private var latestConfiguration: Configuration? = null

    private val isFirstActivity: Boolean
        get() = activityCounter == 1

    private val isLastActivity: Boolean
        get() = activityCounter == 0

    internal fun initialize(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
        application.registerComponentCallbacks(this)
    }

    internal fun setForegroundScopeObserver(observer: LifecycleObserver) {
        foregroundScopeObserver = observer
    }

    override fun addObserver(observer: LifecycleObserver) {
        observers += observer
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observers -= observer
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: " + activity.localClassName)
        latestConfiguration = null
        activityCounter++

        if (isFirstActivity && !anyActivityStarted) {
            foregroundScopeObserver?.onFirstActivityStarted(activity)
            observers.forEach { it.onFirstActivityStarted(activity) }
            anyActivityStarted = true
        }
        foregroundScopeObserver?.onAnyActivityStarted(activity)
        observers.forEach { it.onAnyActivityStarted(activity) }
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "onActivityStopped: " + activity.localClassName)
        activityCounter--

        observers.forEach { it.onAnyActivityStopped(activity) }
        foregroundScopeObserver?.onAnyActivityStopped(activity)
        if (isLastActivity && !pausedWithConfig) {
            observers.forEach { it.onLastActivityStopped(activity) }
            foregroundScopeObserver?.onLastActivityStopped(activity)
            latestConfiguration = null
            anyActivityStarted = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        latestConfiguration = newConfig
        if (resumed) {
            foregroundScopeObserver?.onConfigurationChanged(newConfig)
            observers.forEach { it.onConfigurationChanged(newConfig) }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        resumed = false
        pausedWithConfig = latestConfiguration != null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityResumed(activity: Activity) {
        resumed = true
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onLowMemory() {}

    override fun onTrimMemory(level: Int) {}
}
