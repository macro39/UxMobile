package sk.uxtweak.uxmobile

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.util.toJson
import java.io.IOException

class EventServer(
    private val socket: WebSocketClient
) {
    private var onConnectedCallback: () -> Unit = {}

    init {
        GlobalScope.launch {
            socket.connect()
        }
        socket.setOnConnected {
            onConnectedCallback()
        }
    }

    fun setOnConnected(callback: () -> Unit) {
        onConnectedCallback = callback
    }

    suspend fun trySendEvent(event: SessionEvent)
        = socket.tryEmit(EVENTS_CHANNEL_NAME, event.toJson())

    suspend fun generateSessionId(): String? {
        try {
            return socket.emit(SESSION_ID_CHANNEL).toString()
        } catch (exception: IOException) {
            Log.e(TAG, "Cannot connect to events WS server")
        }
        return null
    }

    companion object {
        const val TAG = "UxMobile"
        const val EVENTS_CHANNEL_NAME = "events"
        const val SESSION_ID_CHANNEL = "session/id"
    }
}
