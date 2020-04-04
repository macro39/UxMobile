package sk.uxtweak.uxmobile.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import sk.uxtweak.uxmobile.concurrency.ChannelCondition
import sk.uxtweak.uxmobile.concurrency.Condition
import sk.uxtweak.uxmobile.util.logd
import sk.uxtweak.uxmobile.util.loge
import sk.uxtweak.uxmobile.util.logi

class ConnectionManager(private val socket: WebSocketClient) {
    private val condition: Condition =
        ChannelCondition()
    private val onConnectedOnceListeners = mutableListOf<() -> Unit>()
    private var onConnectedListener: () -> Unit = {}
    private var onDisconnectedListener: () -> Unit = {}

    fun startAutoConnection() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket.autoReconnect = true
                socket.setOnConnected(::onSocketConnected)
                socket.setOnDisconnected(::onSocketDisconnected)
                logi(
                    TAG,
                    "Connecting to events WebSocket server"
                )
                socket.connect()
                logi(
                    TAG,
                    "Connected to events WebSocket server"
                )
            } catch (exception: Exception) {
                loge(
                    TAG,
                    "Exception occurred while executing context(${exception.message.toString()}): "
                )
            }
        }
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

    suspend fun waitUntilConnected() {
        if (!socket.isConnected) {
            logd(TAG, "Waiting for connection")
            condition.block()
            logd(TAG, "Ended waiting for connection")
        }
    }

    suspend fun suspendUntilConnected() = suspendCancellableCoroutine<Unit> {
        if (!socket.isConnected) {
            logd(TAG, "Suspending for connection")
            addOnConnectedOnce {
                logd(
                    TAG,
                    "Ended suspend for connection"
                )
                it.resumeWith(Result.success(Unit))
            }
        }
        it.resumeWith(Result.success(Unit))
    }

    private fun onSocketConnected() {
        logi(TAG, "WebSocket connected to server")
        condition.open()
        onConnectedOnceListeners.forEach { it() }
        onConnectedOnceListeners.clear()
        onConnectedListener()
    }

    private fun onSocketDisconnected() {
        logi(TAG, "WebSocket disconnected from server")
        onDisconnectedListener()
    }

    companion object {
        private const val TAG = "UxMobile"
    }
}
