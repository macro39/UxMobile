package sk.uxtweak.uxmobile.net

import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.concurrency.IOContext
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd
import sk.uxtweak.uxmobile.util.loge
import sk.uxtweak.uxmobile.util.logi
import kotlin.coroutines.CoroutineContext

class ConnectionManager(private val socket: WebSocketClient) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = reusableContext
    private lateinit var reusableContext: CoroutineContext

    var isRunning: Boolean = false
        private set

    val isConnected: Boolean
        get() = socket.isConnected

    private val onConnectedOnceListeners = mutableListOf<() -> Unit>()
    private var onConnectedListener: () -> Unit = {}
    private var onDisconnectedListener: () -> Unit = {}

    init {
        socket.setOnConnected(::onSocketConnected)
        socket.setOnDisconnected(::onSocketDisconnected)
    }

    fun start() {
        reusableContext = IOContext()
        isRunning = true
        launch {
            try {
                socket.autoReconnect = true
                logi(TAG, "Connecting to events WebSocket server")
                socket.connect()
            } catch (exception: Exception) {
                loge(TAG, "Exception occurred while executing context(${exception.message.toString()}): ")
            }
        }
    }

    fun stop() {
        logi(TAG, "Disconnecting from events WebSocket server")
        socket.autoReconnect = false
        socket.disconnect()
        cancel()
        isRunning = false
    }

    private fun addOnConnectedOnce(listener: () -> Unit) {
        onConnectedOnceListeners += listener
    }

    fun setOnConnected(listener: () -> Unit) {
        onConnectedListener = listener
    }

    fun setOnDisconnected(listener: () -> Unit) {
        onDisconnectedListener = listener
    }

    suspend fun emit(event: String, message: Any = "") = socket.emit(event, message)

    suspend fun waitUntilConnected() = suspendCancellableCoroutine<Unit> {
        if (!isConnected) {
            logd(TAG, "Suspending for connection")
            addOnConnectedOnce {
                logd(TAG, "Ended suspend for connection")
                it.resumeWith(Result.success(Unit))
            }
        } else {
            it.resumeWith(Result.success(Unit))
        }
    }

    private fun onSocketConnected() {
        logi(TAG, "WebSocket connected to server")
        onConnectedOnceListeners.forEach { it() }
        onConnectedOnceListeners.clear()
        onConnectedListener()
    }

    private fun onSocketDisconnected() {
        logi(TAG, "WebSocket disconnected from server")
        onDisconnectedListener()
    }
}
