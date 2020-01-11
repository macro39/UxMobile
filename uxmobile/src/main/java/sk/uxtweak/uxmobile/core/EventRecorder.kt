package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.content.res.Configuration
import android.os.SystemClock
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import sk.uxtweak.uxmobile.SessionExceptionHandler
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.adapter.WindowCallbackAdapter
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle.currentActivity
import sk.uxtweak.uxmobile.model.ViewEnum
import sk.uxtweak.uxmobile.model.event.*
import sk.uxtweak.uxmobile.util.ViewUtils

class EventRecorder : LifecycleObserverAdapter(), GestureDetector.OnGestureListener {
    private var isRecording = false
    private var startTime = 0L
    private var orientation = 0
    private var configurationRecentlyChanged = false

    private lateinit var gestureDetector: GestureDetector

    private val eventListeners = mutableListOf<(Event) -> Unit>()
    private val sessionStartedListeners = mutableListOf<() -> Unit>()
    private val sessionEndedListeners = mutableListOf<() -> Unit>()

    private val elapsedTime: Long
        get() = SystemClock.elapsedRealtime() - startTime

    private val rootView: View
        get() = currentActivity?.get()!!.window.decorView.rootView

    init {
        ApplicationLifecycle.addObserver(this)
        registerExceptionHandler()
    }

    fun addListener(
        eventListener: (Event) -> Unit = {},
        sessionStartListener: () -> Unit = {},
        sessionEndListener: () -> Unit = {}
    ) {
        eventListeners += eventListener
        sessionStartedListeners += sessionStartListener
        sessionEndedListeners += sessionEndListener
    }

    fun removeListener(
        eventListener: (Event) -> Unit = {},
        sessionStartListener: () -> Unit = {},
        sessionEndListener: () -> Unit = {}
    ) {
        eventListeners -= eventListener
        sessionStartedListeners -= sessionStartListener
        sessionEndedListeners -= sessionEndListener
    }

    private fun startRecording() {
        if (isRecording) {
            Log.w(TAG, "Recording is already running. First call stopRecording() to start again")
            return
        }
        isRecording = true
        startTime = SystemClock.elapsedRealtime()
    }

    private fun stopRecording() {
        if (!isRecording) {
            Log.w(TAG, "Recording is not running. First call startRecording()")
            return
        }
        notifyListeners(SessionEndEvent(elapsedTime))
        isRecording = false
    }

    override fun onFirstActivityStarted(activity: Activity) {
        orientation = activity.resources.configuration.orientation
        gestureDetector = GestureDetector(activity.applicationContext, this)
        startRecording()
        sessionStartedListeners.forEach { it() }
    }

    override fun onEveryActivityStarted(activity: Activity) {
        handleConfigurationChange(activity)
        updateActivityCallback(activity)
    }

    private fun handleConfigurationChange(activity: Activity) {
        if (!configurationRecentlyChanged) {
            notifyListeners(ActivityStartedEvent(elapsedTime, activity.localClassName))
        }
        configurationRecentlyChanged = false
    }

    private fun updateActivityCallback(activity: Activity) {
        val previousCallback = activity.window.callback
        if (previousCallback is WindowCallbackAdapter) {
            return
        }

        activity.window.callback = object : WindowCallbackAdapter(previousCallback) {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(event)
                return super.dispatchTouchEvent(event)
            }
        }
    }

    override fun onLastActivityStopped(activity: Activity) {
        stopRecording()
        sessionEndedListeners.forEach { it() }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        configurationRecentlyChanged = true

        if (orientation != configuration.orientation) {
            orientation = configuration.orientation
            notifyListeners(OrientationEvent(elapsedTime, orientation))
        }
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val touchedView = ViewUtils.getTouchedView(e, rootView)
        Log.d(TAG, "Touched view: $touchedView")

        notifyListeners(
            ClickEvent(
                elapsedTime,
                e.x / rootView.width,
                e.y / rootView.height,
                ViewEnum.fromView(touchedView),
                ViewUtils.getViewText(touchedView),
                ViewUtils.getViewValue(touchedView)
            )
        )

        return false
    }

    override fun onLongPress(e: MotionEvent) {
        val touchedView = ViewUtils.getTouchedView(e, rootView)

        notifyListeners(
            LongPressEvent(
                elapsedTime,
                e.x / rootView.width,
                e.y / rootView.height,
                ViewEnum.fromView(touchedView),
                ViewUtils.getViewText(touchedView),
                ViewUtils.getViewValue(touchedView)
            )
        )
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        notifyListeners(
            ScrollEvent(
                elapsedTime,
                e2.x / rootView.width,
                e2.y / rootView.height,
                distanceX / rootView.width,
                distanceY / rootView.height
            )
        )

        return false
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        notifyListeners(
            FlingEvent(
                elapsedTime,
                e2.x / rootView.width,
                e2.y / rootView.height,
                velocityX / rootView.width,
                velocityY / rootView.height
            )
        )

        return false
    }

    override fun onDown(e: MotionEvent) = false
    override fun onShowPress(e: MotionEvent) {}

    private fun registerExceptionHandler() {
        SessionExceptionHandler.register()
        SessionExceptionHandler.handler?.let {
            it.setListener { _, throwable ->
                notifyListeners(ExceptionEvent(elapsedTime, throwable))
            }
        }
    }

    private fun notifyListeners(event: Event) {
        if (isRecording) {
            Log.d(TAG, "Event recorded: ${event.toJson()}")
            eventListeners.forEach { it(event) }
        }
    }

    companion object {
        private const val TAG = "UxMobile"
    }
}
