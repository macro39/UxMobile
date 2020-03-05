package sk.uxtweak.uxmobile.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.logd
import sk.uxtweak.uxmobile.loge
import sk.uxtweak.uxmobile.logi
import sk.uxtweak.uxmobile.net.WebSocketClient

class ConnectionManager(private val socket: WebSocketClient) {
    private val condition = SimpleCondition()
    private var onConnectedListener: () -> Unit = {}
    private var onDisconnectedListener: () -> Unit = {}

    fun startAutoConnection() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                socket.autoReconnect = true
                socket.setOnConnected(::onSocketConnected)
                socket.setOnDisconnected(::onSocketDisconnected)
                logi(TAG, "Connecting to events WebSocket server")
                socket.connect()
                logi(TAG, "Connected to events WebSocket server")
            } catch (exception: Exception) {
                loge(TAG, "Exception occurred while executing context(${exception.message.toString()}): ")
            }
        }
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

    private fun onSocketConnected() {
        logi(TAG, "WebSocket connected to server")
        onConnectedListener()
        condition.open()
    }

    private fun onSocketDisconnected() {
        logi(TAG, "WebSocket disconnected from server")
        onDisconnectedListener()
    }

    companion object {
        private val TAG = ConnectionManager::class.java.simpleName
    }
}
