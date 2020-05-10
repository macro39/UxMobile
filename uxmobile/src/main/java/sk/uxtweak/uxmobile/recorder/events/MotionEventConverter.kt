package sk.uxtweak.uxmobile.recorder.events

import android.view.GestureDetector
import android.view.MotionEvent
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.util.getTouchedView
import sk.uxtweak.uxmobile.util.viewText
import sk.uxtweak.uxmobile.util.viewType
import sk.uxtweak.uxmobile.util.viewValue

/**
 * When registered with [GestureDetector], this class receives motion events, then converts them to
 * [Event] and dispatches them to registered listeners.
 */
class MotionEventConverter(
    private val eventListener: (Event) -> Unit
) : GestureDetector.SimpleOnGestureListener() {
    override fun onDoubleTap(e: MotionEvent): Boolean {
        val view = ForegroundActivityHolder.foregroundActivity?.window?.decorView?.getTouchedView(e)
        eventListener(Event.DoubleTapEvent(e.x, e.y, view.viewType, view.viewText, view.viewValue))
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val view = ForegroundActivityHolder.foregroundActivity?.window?.decorView?.getTouchedView(e)
        eventListener(Event.TapEvent(e.x, e.y, view.viewType, view.viewText, view.viewValue))
        return false
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val view = ForegroundActivityHolder.foregroundActivity?.window?.decorView?.getTouchedView(e2)
        eventListener(Event.FlingEvent(e2.x, e2.y, velocityX, velocityY, view.viewType, view.viewText, view.viewValue))
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val view = ForegroundActivityHolder.foregroundActivity?.window?.decorView?.getTouchedView(e2)
        eventListener(Event.ScrollEvent(e2.x, e2.y, distanceX, distanceY, view.viewType, view.viewText, view.viewValue))
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        val view = ForegroundActivityHolder.foregroundActivity?.window?.decorView?.getTouchedView(e)
        eventListener(Event.LongPressEvent(e.x, e.y, view.viewType, view.viewText, view.viewValue))
    }
}
