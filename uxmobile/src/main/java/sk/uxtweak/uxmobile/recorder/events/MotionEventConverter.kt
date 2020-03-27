package sk.uxtweak.uxmobile.recorder.events

import android.view.GestureDetector
import android.view.MotionEvent
import sk.uxtweak.uxmobile.model.Event

/**
 * When registered with [GestureDetector], this class receives motion events, then converts them to
 * [Event] and dispatches them to registered listeners.
 */
class MotionEventConverter(
    private val eventListener: (Event) -> Unit
) : GestureDetector.SimpleOnGestureListener() {
    override fun onDoubleTap(e: MotionEvent): Boolean {
        eventListener(Event.DoubleTapEvent(e.x, e.y))
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        eventListener(Event.TapEvent(e.x, e.y))
        return false
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        eventListener(Event.FlingEvent(e2.x, e2.y, velocityX, velocityY))
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        eventListener(Event.ScrollEvent(e2.x, e2.y, distanceX, distanceY))
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        eventListener(Event.LongPressEvent(e.x, e.y))
    }
}
