package sk.uxtweak.uxmobile.lifecycle

interface Lifecycle {
    fun addObserver(observer: LifecycleObserver)
    fun removeObserver(observer: LifecycleObserver)
}
