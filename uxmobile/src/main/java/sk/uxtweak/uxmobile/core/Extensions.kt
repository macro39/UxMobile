package sk.uxtweak.uxmobile.core

import android.app.Application
import android.content.Context
import android.graphics.Rect
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.children
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.nio.ByteBuffer
import java.text.DecimalFormat
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

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.withFixedDelay(
    context: CoroutineContext = EmptyCoroutineContext,
    timeMillis: Long,
    block: suspend CoroutineScope.() -> Unit
) = launch(newCoroutineContext(context)) {
    while (isActive) {
        block()
        delay(timeMillis)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.atFixedRate(
    context: CoroutineContext = EmptyCoroutineContext,
    rate: Long,
    block: suspend CoroutineScope.() -> Unit
) = launch(newCoroutineContext(context)) {
    var start: Long
    while (isActive) {
        start = SystemClock.elapsedRealtime()
        block()
        delay((start + rate) - SystemClock.elapsedRealtime())
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <E> Channel<E>.toCurrentList(): List<E> = mutableListOf<E>().apply {
    while (!isEmpty) {
        this += receive()
    }
}

fun ByteBuffer.copy(): ByteBuffer {
    val copy = ByteBuffer.allocate(limit())
    copy.put(this)
    return copy
}

private val displayMetrics = DisplayMetrics()

val Application.displaySize: Size
    get() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels with displayMetrics.heightPixels
    }

private val humanFormat = DecimalFormat("0.##")

fun Long.toHumanUnit(): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")
    var remaining = this.toFloat()
    var timesDivided = 0
    while (remaining >= 1024) {
        remaining /= 1024
        timesDivided++
    }
    return "${humanFormat.format(remaining)} ${units[timesDivided]}"
}

fun Int.toHumanUnit() = toLong().toHumanUnit()
