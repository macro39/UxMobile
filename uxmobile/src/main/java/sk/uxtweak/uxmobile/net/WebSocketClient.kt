package sk.uxtweak.uxmobile.net

import com.neovisionaries.ws.client.WebSocketFrame
import io.github.sac.ReconnectStrategy
import io.github.sac.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import sk.uxtweak.uxmobile.core.Stats
import java.io.IOException
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WebSocketClient(url: String) : BasicListenerAdapter() {
    var autoReconnect: Boolean = false
        set(value) {
            field = value
            if (value) enableAutoReconnect() else disableAutoReconnect()
        }
    val isConnected: Boolean
        get() = socket.isconnected()

    private val socket = Socket(url)

    private var onConnectedListener: () -> Unit = {}
    private var onDisconnectedListener: () -> Unit = {}

    init {
        socket.setListener(object : BasicListenerAdapter() {
            override fun onConnected(
                socket: Socket,
                headers: MutableMap<String, MutableList<String>>?
            ) {
                Stats.onConnected()
                onConnectedListener()
            }

            override fun onDisconnected(
                socket: Socket,
                serverCloseFrame: WebSocketFrame?,
                clientCloseFrame: WebSocketFrame?,
                closedByServer: Boolean
            ) {
                Stats.onDisconnected()
                onDisconnectedListener()
            }
        })
    }

    fun setOnConnected(onConnected: () -> Unit) {
        onConnectedListener = onConnected
    }

    fun setOnDisconnected(onDisconnected: () -> Unit) {
        onDisconnectedListener = onDisconnected
    }

    suspend fun connect() = withContext(Dispatchers.IO) { socket.connect() }

    fun disconnect() = socket.disconnect()

    suspend fun emit(event: String, message: Any = ""): Any? {
        return withTimeoutOrNull(EMIT_TIMEOUT) {
            suspendCancellableCoroutine<Any?> {
                if (!isConnected) {
                    it.resumeWithException(IOException("Socket is not connected"))
                }
                socket.emit(event, message) { _, error, data ->
                    if (error == null) {
                        it.resume(data ?: "")
                    } else {
                        it.resumeWithException(EmitException(error.toString()))
                    }
                }
            }
        } ?: throw TimeoutException("Timeout exceeded when emitting event")
    }

    suspend fun tryEmit(event: String, message: Any = ""): Boolean {
        return withTimeoutOrNull(EMIT_TIMEOUT) {
            suspendCancellableCoroutine<Boolean> {
                if (!isConnected) {
                    it.resume(false)
                }
                socket.emit(event, message) { _, error, _ ->
                    it.resume(error == null)
                }
            }
        } ?: false
    }

    private fun enableAutoReconnect() {
        socket.setReconnection(
            ReconnectStrategy(
                RECONNECT_INTERVAL,
                MAX_RECONNECT_INTERVAL,
                RECONNECT_DECAY,
                MAX_ATTEMPTS
            )
        )
    }

    private fun disableAutoReconnect() {
        socket.setReconnection(null)
    }

    companion object {
        /**
         * First reconnect interval
         */
        private const val RECONNECT_INTERVAL = 2000

        /**
         * Maximum reconnect interval
         */
        private const val MAX_RECONNECT_INTERVAL = 30_000

        /**
         * How many times to increase the reconnect interval on unsuccessful connect attempt
         * (until max reconnect interval is reached)
         */
        private const val RECONNECT_DECAY = 2.0F

        /**
         * How many times try to reconnect (-1 for unlimited)
         */
        private const val MAX_ATTEMPTS = -1

        /**
         * Time until ACK must be received when emitting event
         */
        private const val EMIT_TIMEOUT = 15000L
    }
}
