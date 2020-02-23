package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.ForegroundScope
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.copy
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.model.events.Event
import sk.uxtweak.uxmobile.toHumanUnit
import java.nio.ByteBuffer

class EventsController(
    eventRecorder: EventRecorder,
    videoRecorder: VideoRecorder,
    private val serverManager: ServerManager
) : LifecycleObserverAdapter() {
    private var sessionId: String? = null
    private val unsentEvents = mutableListOf<SessionEvent>()

    init {
        eventRecorder.addOnEventListener(::onEvent)
        videoRecorder.setBufferReadyListener(::onBufferReady)
    }

    override fun onFirstActivityStarted(activity: Activity) {
        generateSessionId()
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.StartEvent))
    }

    override fun onLastActivityStopped(activity: Activity) {
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.EndEvent))
    }

    private fun onEvent(event: Event) {
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), event))
    }

    private fun onBufferReady(buffer: ByteBuffer) {
        Log.d(TAG, "Video buffer received: ${buffer.limit().toHumanUnit()}")
        val data = Base64.encodeToString(buffer.copy().array(), Base64.DEFAULT)
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.VideoChunkEvent(data)))
    }

    private fun generateSessionId() = ForegroundScope.launch(Dispatchers.IO) {
        while (isActive && sessionId == null) {
            try {
                sessionId = serverManager.generateSessionId()
                Log.d(TAG, "Got session ID $sessionId")
            } catch (exception: Exception) {
                Log.w(TAG, "Cannot generate session ID", exception)
                delay(FAILED_REQUEST_TIMEOUT)
            }
        }
        unsentEvents.forEach {
            Log.d(TAG, "Sending cached events that were without ID")
            it.sessionId = sessionId
            serverManager.addToQueue(it)
        }
    }

    private fun sendEvent(sessionEvent: SessionEvent) {
        if (sessionEvent.sessionId != null) {
            serverManager.addToQueue(sessionEvent)
        } else {
            unsentEvents += sessionEvent
        }
    }

    companion object {
        const val FAILED_REQUEST_TIMEOUT = 2000L

        private const val TAG = "UxMobile"
    }
}
