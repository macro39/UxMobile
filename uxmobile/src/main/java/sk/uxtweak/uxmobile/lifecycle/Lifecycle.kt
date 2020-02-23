package sk.uxtweak.uxmobile.lifecycle

import sk.uxtweak.uxmobile.core.LifecycleObserver

interface Lifecycle {
    fun addObserver(observer: LifecycleObserver)
    fun removeObserver(observer: LifecycleObserver)
}
