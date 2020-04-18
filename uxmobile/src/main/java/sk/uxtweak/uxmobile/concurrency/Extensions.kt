package sk.uxtweak.uxmobile.concurrency

import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

fun requireMainThread() {
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
        throw IllegalStateException("Called from wrong thread! Must be called from main thread.")
    }
}

@Suppress("FunctionName")
fun MainContext(): CoroutineContext = SupervisorJob() + Dispatchers.Main

@Suppress("FunctionName")
fun IOContext(): CoroutineContext = SupervisorJob() + Dispatchers.IO
