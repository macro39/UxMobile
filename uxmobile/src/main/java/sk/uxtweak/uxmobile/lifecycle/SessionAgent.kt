package sk.uxtweak.uxmobile.lifecycle

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.ForegroundScope
import sk.uxtweak.uxmobile.core.EventRecorder
import sk.uxtweak.uxmobile.model.event.Event
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.repository.LocalEventStore
import java.util.*

class SessionAgent(
    private val eventRecorder: EventRecorder,
    private val webSocket: WebSocketClient,
    private val eventStore: LocalEventStore
) {
    private val events = LinkedList<Event>()

    init {
        webSocket.setConnectListener(::onConnectionSuccessful)
        eventRecorder.addListener(::onEventCaptured, ::onSessionStart, ::onSessionEnd)
    }

    private fun onConnectionSuccessful() {
        ForegroundScope.launch { sendAllLocalEvents() }
    }

    private suspend fun sendAllLocalEvents() {
        while (events.isNotEmpty()) {
            val event = events.element()
            Log.d(TAG, "Sending event in memory: $event")
            if (!webSocket.emitEvent(event)) {
                Log.d(TAG, "Cannot send all events (remaining: ${events.size})")
                break
            }
            events.remove()
        }

        if (events.isEmpty()) {
            Log.d(TAG, "All in memory events sent, sending locally stored events")
            sendLocalEvents()
        }
    }

    private fun onSessionStart() {
        events.clear()
    }

    private fun onSessionEnd() {
        Log.d(TAG, "Session ended, storing all in memory events to database (count: ${events.size})")
        ForegroundScope.launch {
            eventStore.storeEvents(events.toList())
        }
    }

    private fun onEventCaptured(event: Event) {
        Log.d(TAG, "onEventCaptured: ${event.toJson()}")
        GlobalScope.launch {
            if (!webSocket.emitEvent(event)) {
                Log.d(TAG, "Cannot send event to server, storing locally: $event")
                storeEventLocally(event)
            } else {
                Log.d(TAG, "Sending event to server: $event")
            }
        }
    }

    private suspend fun storeEventLocally(event: Event) {
        if (events.size >= MAX_EVENTS_IN_MEMORY) {
            Log.d(TAG, "Flushing events to database")
            eventStore.storeEvents(events.toList())
            events.clear()
        } else {
            Log.d(TAG, "Storing event in memory (events count: ${events.size + 1})")
        }
        events += event
    }

    private suspend fun sendLocalEvents() {
        val sentEvents = mutableListOf<Event>()
        val localEvents = eventStore.getAllLocalEvents()
        for (event in localEvents) {
            Log.d(TAG, "Sending locally stored event: $event")
            if (!webSocket.emitEvent(event)) {
                Log.d(TAG, "Cannot send all local events (remaining: ${events.size}")
                break
            }
            sentEvents += event
        }
        eventStore.deleteEvents(sentEvents)
    }

    companion object {
        private const val TAG = "UxMobile"
        private const val MAX_EVENTS_IN_MEMORY = 512
    }
}
