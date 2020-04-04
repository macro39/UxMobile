package sk.uxtweak.uxmobile.lifecycle

interface Lifecycle {
    fun addObserver(observer: LifecycleObserver)
    fun removeObserver(observer: LifecycleObserver)
}

operator fun Lifecycle.plusAssign(observer: LifecycleObserver) = addObserver(observer)
operator fun Lifecycle.minusAssign(observer: LifecycleObserver) = removeObserver(observer)
