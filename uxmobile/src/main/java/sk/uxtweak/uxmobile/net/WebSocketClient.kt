package sk.uxtweak.uxmobile.net

import io.github.sac.ReconnectStrategy
import io.github.sac.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.uxtweak.uxmobile.lifecycle.SessionCoroutineScope
import sk.uxtweak.uxmobile.model.event.Event

class WebSocketClient(url: String) : SessionCoroutineScope() {
    private val socket = Socket(url)
    private var connectListener: () -> Unit = {}

    private val basicListener = object : BasicListenerAdapter() {
        override fun onConnected(
            socket: Socket?,
            headers: MutableMap<String, MutableList<String>>?
        ) {
            connectListener()
        }
    }

    init {
        socket.setReconnection(
            ReconnectStrategy(
                RECONNECT_INTERVAL,
                MAX_RECONNECT_INTERVAL,
                RECONNECT_DECAY,
                MAX_ATTEMPTS
            )
        )
        socket.setListener(basicListener)
    }

    fun connect() = launch { connectInternal() }

    private suspend fun connectInternal() = withContext(Dispatchers.IO) { socket.connect() }

    fun setConnectListener(listener: () -> Unit) {
        connectListener = listener
    }

    fun sendEvent(event: Event): Boolean {
        if (socket.isconnected()) {
            socket.emit(EVENT_NAME, event.toJson())
            return socket.isconnected()
        }
        return false
    }

    fun sendRaw(event: String, data: String): Boolean {
        if (socket.isconnected()) {
            socket.emit(event, data)
            return socket.isconnected()
        }
        return false
    }

    companion object {
        private const val EVENT_NAME = "events"
        private const val RECONNECT_INTERVAL = 2000
        private const val MAX_RECONNECT_INTERVAL = 30000
        private const val RECONNECT_DECAY = 2.0F
        private const val MAX_ATTEMPTS = -1
    }
}
