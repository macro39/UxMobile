package sk.uxtweak.uxmobile.util

import android.util.Log

object LongLog {
    @JvmStatic
    fun d(tag: String, message: String) {
        val maxLogSize = 1000
        for (i in 0..(message.length / maxLogSize)) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > message.length) message.length else end
            Log.d(tag, message.substring(start, end))
        }
    }
}
