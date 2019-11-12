package sk.uxtweak.uxmobile

import android.graphics.Rect
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun View.findViewAt(x: Int, y: Int): View? {
    if (this is ViewGroup) {
        for (child in children) {
            if (child is ViewGroup) {
                val view = child.findViewAt(x, y)
                if (view != null && view.isShown) {
                    return view
                }
            } else {
                val bounds = Rect()
                getHitRect(bounds)
                if (bounds.contains(x, y)) return this
            }
        }
    }
    return null
}

fun CoroutineScope.atFixedRate(
    context: CoroutineContext = EmptyCoroutineContext,
    rate: Long,
    block: suspend CoroutineScope.() -> Unit
) = launch(context) {
    var start: Long
    while (isActive) {
        start = SystemClock.elapsedRealtime()
        block()
        delay((start + rate) - SystemClock.elapsedRealtime())
    }
}
