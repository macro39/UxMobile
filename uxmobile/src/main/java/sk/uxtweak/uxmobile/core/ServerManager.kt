package sk.uxtweak.uxmobile.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.rpc.RpcManager
import sk.uxtweak.uxmobile.server.SessionService
import sk.uxtweak.uxmobile.util.toJson

class ServerManager(private val socket: WebSocketClient) {
    private val events = Channel<SessionEvent>(BUFFER_SIZE)
    private val condition = SimpleCondition()
    private val rpcManager = RpcManager(socket)
    private val sessionService = rpcManager.create(SessionService::class.java)

    fun startLoop() {
        GlobalScope.launch(Dispatchers.IO) {
            socket.autoReconnect = true
            socket.setOnConnected(::onSocketConnected)
            socket.connect()
            loopSendingAllMessages()
        }
    }

    suspend fun generateSessionId(): String {
        waitForConnection()
        return sessionService.generateSessionId()
    }

    fun addToQueue(event: SessionEvent) = events.offer(event)

    @UseExperimental(ExperimentalCoroutinesApi::class)
    private suspend fun loopSendingAllMessages() {
        for (event in events) {
            waitForConnection()
            if (!socket.tryEmit(CHANNEL_NAME, event.toJson())) {
                addToQueue(event)
                delay(FAILED_SENT_DELAY)
            } else {
                Log.d("UxMobile", "Event sent successfully ($event)")
            }
        }
    }

    private suspend fun waitForConnection() {
        if (!socket.isConnected) {
            Log.d("UxMobile", "Suspending thread")
            condition.block()
        }
    }

    private fun onSocketConnected() {
        condition.open()
    }

    companion object {
        const val BUFFER_SIZE = Channel.UNLIMITED
        const val CHANNEL_NAME = "events"
        const val FAILED_SENT_DELAY = 2000L
    }
}
