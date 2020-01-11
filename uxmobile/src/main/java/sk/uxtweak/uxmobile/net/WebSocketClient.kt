package sk.uxtweak.uxmobile.net

import com.neovisionaries.ws.client.WebSocketFrame
import io.github.sac.ReconnectStrategy
import io.github.sac.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import sk.uxtweak.uxmobile.ForegroundScope
import sk.uxtweak.uxmobile.model.event.Event
import java.util.concurrent.atomic.AtomicInteger

class WebSocketClient(url: String) : BasicListenerAdapter() {
    private val socket = Socket(url)
    private var connectListener: () -> Unit = {}
    private val counter = AtomicInteger(1)
    private val acks = mutableMapOf<Int, Channel<Unit>>()

    private val basicListener = object : BasicListenerAdapter() {
        override fun onConnected(
            socket: Socket,
            headers: MutableMap<String, MutableList<String>>?
        ) {
            registerListenerForAck()
            connectListener()
        }

        override fun onDisconnected(
            socket: Socket?,
            serverCloseFrame: WebSocketFrame?,
            clientCloseFrame: WebSocketFrame?,
            closedByServer: Boolean
        ) {
            unregisterListenerForAck()
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

    suspend fun connect() = withContext(Dispatchers.IO) { socket.connect() }

    fun setConnectListener(listener: () -> Unit) {
        connectListener = listener
    }

    private suspend fun waitForAck(cid: Int): Boolean {
        val channel = Channel<Unit>()
        acks[cid] = channel
        return withTimeoutOrNull(ACK_TIMEOUT) { channel.receive() } != null
    }

    suspend fun emit(event: String, message: Any): Boolean {
        if (!socket.isconnected()) return false
        val cid = counter.getAndIncrement()
        socket.emit(event, "$cid|$message")
        return waitForAck(cid)
    }

    suspend fun emitEvent(event: Event): Boolean {
        return emit(EVENT_NAME, event.toJson())
    }

    private fun registerListenerForAck() {
        socket.on(ACK_EVENT_NAME) { _, data ->
            ForegroundScope.launch { acks[data as Int]?.send(Unit) }
        }
    }

    private fun unregisterListenerForAck() {
        socket.removeEmitCallback(ACK_EVENT_NAME)
    }

    companion object {
        private const val EVENT_NAME = "events"
        private const val RECONNECT_INTERVAL = 2000
        private const val MAX_RECONNECT_INTERVAL = 30000
        private const val RECONNECT_DECAY = 2.0F
        private const val MAX_ATTEMPTS = -1

        private const val ACK_EVENT_NAME = "ack"
        private const val ACK_TIMEOUT = 5000L
    }
}
