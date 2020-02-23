package sk.uxtweak.uxmobile

import android.app.Activity
import android.graphics.Rect
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

data class Size(val width: Int, val height: Int)

infix fun Int.with(other: Int) = Size(this, other)

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

fun ByteBuffer.copy(): ByteBuffer {
    val copy = ByteBuffer.allocate(limit())
    copy.put(this)
    return copy
}

private val displayMetrics = DisplayMetrics()

val Activity.displaySize: Size
    get() {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels with displayMetrics.heightPixels
    }
