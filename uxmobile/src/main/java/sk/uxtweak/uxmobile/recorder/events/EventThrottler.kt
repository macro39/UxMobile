package sk.uxtweak.uxmobile.recorder.events

import android.os.SystemClock

class EventThrottler(private val delay: Long) {
    private var lastEvent = 0L

    /**
     * Throttles event.
     * @return whether event should be throttled (not dispatched)
     */
    fun throttle(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now - lastEvent < delay) {
            return true
        }
        lastEvent = now
        return false
    }
}
