package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.os.SystemClock
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.*
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.model.events.Event
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.server.ServerServices
import java.nio.ByteBuffer

class EventsController(
    eventRecorder: EventRecorder,
    private val videoRecorder: VideoRecorder,
    private val eventsSocket: WebSocketClient,
    private val eventLoop: EventLooper
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
        videoRecorder.start()
    }

    override fun onLastActivityStopped(activity: Activity) {
        videoRecorder.stop()
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), Event.EndEvent))
    }

    private fun onEvent(event: Event) {
        sendEvent(SessionEvent(sessionId, SystemClock.elapsedRealtime(), event))
    }

    private fun onBufferReady(buffer: ByteBuffer) {
        logd(TAG, "Video buffer received: ${buffer.limit().toHumanUnit()}")
        val data = Base64.encodeToString(buffer.copy().array(), Base64.DEFAULT)
        sendEvent(
            SessionEvent(
                sessionId,
                SystemClock.elapsedRealtime(),
                Event.VideoChunkEvent(data)
            )
        )
    }

    private fun generateSessionId() = ForegroundScope.launch(Dispatchers.IO) {
        while (isActive && sessionId == null) {
            if (!eventsSocket.isConnected) {
                delay(FAILED_REQUEST_TIMEOUT)
                continue
            }
            try {
                sessionId = eventsSocket.emit(ServerServices.GENERATE_SESSION_ID).toString()
                logi(TAG, "Got session ID $sessionId")
            } catch (exception: Exception) {
                logw(TAG, "Cannot generate session ID", exception)
                delay(FAILED_REQUEST_TIMEOUT)
            }
        }
        unsentEvents.forEach {
            logi(TAG, "Sending cached events that were without ID")
            it.sessionId = sessionId
            eventLoop.offer(it)
        }
    }

    private fun sendEvent(sessionEvent: SessionEvent) {
        if (sessionEvent.sessionId != null) {
            eventLoop.offer(sessionEvent)
        } else {
            unsentEvents += sessionEvent
        }
    }

    companion object {
        const val FAILED_REQUEST_TIMEOUT = 1000L

        private const val TAG = "UxMobile"
    }
}
