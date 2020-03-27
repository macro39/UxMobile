package sk.uxtweak.uxmobile.concurrency

import android.os.Handler
import android.os.Looper

object Handlers {
    val Main = Handler(Looper.getMainLooper())
}
