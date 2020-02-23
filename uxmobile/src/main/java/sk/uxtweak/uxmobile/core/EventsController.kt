package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.ForegroundScope
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.model.events.Event
import java.nio.ByteBuffer

class EventsController(
    eventRecorder: EventRecorder,
    private val serverManager: ServerManager
) : LifecycleObserverAdapter() {
    private var sessionId: String? = null
    private val unsentEvents = mutableListOf<SessionEvent>()
    private val videoRecorder = VideoRecorder(1080, 1920, 1000, 25)

    init {
        ApplicationLifecycle.addObserver(this)
        eventRecorder.addOnEventListener(::onEvent)
    }

    override fun onFirstActivityStarted(activity: Activity) {
        generateSessionId()
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.StartEvent))
        registerVideoChunkListener()
    }

    override fun onLastActivityStopped(activity: Activity) {
        unregisterVideoChunkListener()
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.EndEvent))
    }

    private fun onEvent(event: Event) {
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), event))
    }

    private fun registerVideoChunkListener() {
        videoRecorder.setBufferReadyListener {
            val copy = ByteBuffer.allocate(it.limit())
            copy.put(it)
            val data = Base64.encodeToString(copy.array(), Base64.DEFAULT)
            sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.VideoChunkEvent(data)))
        }
    }

    private fun unregisterVideoChunkListener() {
        videoRecorder.setBufferReadyListener {  }
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
