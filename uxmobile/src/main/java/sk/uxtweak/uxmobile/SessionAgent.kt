package sk.uxtweak.uxmobile

import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.core.EventRecorder
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.model.events.Event
import java.util.*

class SessionAgent(
    private val eventsServer: EventServer,
    private val eventRecorder: EventRecorder
) {
    private val events = Collections.synchronizedList(LinkedList<SessionEvent>())
    private var sessionId: String? = null

    init {
        eventRecorder.addOnEventListener { GlobalScope.launch { onEventCaptured(it) } }
        eventsServer.setOnConnected {
            GlobalScope.launch {
                if (sessionId == null) {
                    sessionId = eventsServer.generateSessionId()
                }
                trySendAllEvents()
            }
        }
    }

    private suspend fun onEventCaptured(event: Event) {
        Log.d("UxMobile", "onEventCaptured($event)")
        val sessionEvent = SessionEvent(sessionId, SystemClock.elapsedRealtime(), event)

        if (event is Event.SessionStartEvent) {
            events.clear()
            sessionId = eventsServer.generateSessionId()
        }

        if (sessionId != null) {
            trySendAllEvents()
            if (!eventsServer.trySendEvent(sessionEvent)) {
                events.add(sessionEvent)
            }
        } else {
            events.add(sessionEvent)
        }
    }

    @Synchronized
    private suspend fun trySendAllEvents() {
        if (events.isEmpty()) return
        if (sessionId == null) {
            return
        }
        var event = events.firstOrNull()
        while (event != null) {
            event = event.copy(sessionId = sessionId)
            if (!eventsServer.trySendEvent(event)) {
                break
            }
            events.removeAt(0)
            event = events.firstOrNull()
        }
    }
}
