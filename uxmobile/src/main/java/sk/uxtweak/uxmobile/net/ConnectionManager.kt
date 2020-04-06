package sk.uxtweak.uxmobile.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd
import sk.uxtweak.uxmobile.util.loge
import sk.uxtweak.uxmobile.util.logi

class ConnectionManager(private val socket: WebSocketClient) {
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
        GlobalScope.launch(Dispatchers.IO) {
            isRunning = true
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
