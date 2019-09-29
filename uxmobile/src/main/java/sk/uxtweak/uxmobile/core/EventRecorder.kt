package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import org.json.JSONArray
import org.json.JSONException
import sk.uxtweak.uxmobile.adapter.WindowCallbackAdapter
import sk.uxtweak.uxmobile.model.EventRecording
import sk.uxtweak.uxmobile.model.ViewEnum
import sk.uxtweak.uxmobile.model.event.*
import sk.uxtweak.uxmobile.model.study.Task
import sk.uxtweak.uxmobile.util.ViewUtils
import java.util.*

class EventRecorder : BaseRecorder(), GestureDetector.OnGestureListener {
    private val eventRecordings = LinkedList<EventRecording>()
    private lateinit var gestureDetector: GestureDetector

    private var orientation = 0
    private var configurationRecentlyChanged = false

    private var startTime = 0L

    override fun onSessionStarted() {
        super.onSessionStarted()
        onFirstActivityStarted(currentActivity!!)
        onEveryActivityStarted(currentActivity!!)
    }

    override fun onFirstActivityStarted(activity: Activity) {
        super.onFirstActivityStarted(activity)

        startTime = System.currentTimeMillis()

        configurationRecentlyChanged = false
        orientation = activity.resources.configuration.orientation
        eventRecordings.clear()
    }

    override fun onEveryActivityStarted(activity: Activity) {
        super.onEveryActivityStarted(activity)

        if (!configurationRecentlyChanged) {
            eventRecordings.addLast(EventRecording(activity, currentRelativeMillis()))
        }

        configurationRecentlyChanged = false

        val previousCallback = activity.window.callback

        if (previousCallback is WindowCallbackAdapter) {
            return
        }

        activity.window.callback = object : WindowCallbackAdapter(previousCallback) {
            override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
                gestureDetector.onTouchEvent(event)
                return super.dispatchTouchEvent(event)
            }
        }

        gestureDetector = GestureDetector(activity, this)
    }

    override fun onLastActivityStopped(activity: Activity) {
        super.onLastActivityStopped(activity)
        recordEvent(SessionEndEvent(currentRelativeMillis()))
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)

        configurationRecentlyChanged = true

        if (orientation != configuration.orientation) {
            orientation = configuration.orientation

            recordEvent(OrientationEvent(currentRelativeMillis(), orientation))
        }
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val rootView = getRootView()
        val touchedView = ViewUtils.getTouchedView(e, rootView)

        Log.d("UxMobile", "Touched view: $touchedView")

        recordEvent(ClickEvent(
            currentRelativeMillis(),
            e.x / rootView.width,
            e.y / rootView.height,
            ViewEnum.fromView(touchedView),
            ViewUtils.getViewText(touchedView),
            ViewUtils.getViewValue(touchedView)
        ))

        return false
    }

    override fun onLongPress(e: MotionEvent) {
        val rootView = getRootView()
        val touchedView = ViewUtils.getTouchedView(e, rootView)

        recordEvent(LongPressEvent(
            currentRelativeMillis(),
            e.x / rootView.width,
            e.y / rootView.height,
            ViewEnum.fromView(touchedView),
            ViewUtils.getViewText(touchedView),
            ViewUtils.getViewValue(touchedView)
        ))
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val rootView = getRootView()

        recordEvent(ScrollEvent(
            currentRelativeMillis(),
            e2.x / rootView.width,
            e2.y / rootView.height,
            distanceX / rootView.width,
            distanceY / rootView.height
        ))

        return false
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val rootView = getRootView()

        recordEvent(FlingEvent(
            currentRelativeMillis(),
            e2.x / rootView.width,
            e2.y / rootView.height,
            velocityX / rootView.width,
            velocityY / rootView.height
        ))

        return false
    }

    fun addCustomEvent(eventName: String)
            = recordEvent(CustomEvent(currentRelativeMillis(), eventName))

    fun addCustomEvent(eventName: String, payload: Map<String, String>)
            = recordEvent(CustomEvent(currentRelativeMillis(), eventName, payload))

    fun addExceptionEvent(throwable: Throwable)
            = recordEvent(ExceptionEvent(currentRelativeMillis(), throwable))

    fun addTaskEvent(task: Task, status: TaskEvent.Status)
            = recordEvent(TaskEvent(currentRelativeMillis(), task, status))

    override fun onDown(e: MotionEvent) = false
    override fun onShowPress(e: MotionEvent) {}

    private fun recordEvent(event: Event) {
        Log.d("UxMobile", "Event recorded: ${event.toJson()}")
        eventRecordings.last.addEvent(event)
    }

    private fun currentRelativeMillis() = System.currentTimeMillis() - startTime

    private fun getRootView() = currentActivity!!.window.decorView.rootView

    @Throws(JSONException::class)
    fun getOutput(): JSONArray {
        val out = JSONArray()

        for (recording in eventRecordings) {
            out.put(recording.toJson())
        }

        return out
    }
}
