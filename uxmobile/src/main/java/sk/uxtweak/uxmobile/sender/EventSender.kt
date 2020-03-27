package sk.uxtweak.uxmobile.sender

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import sk.uxtweak.uxmobile.core.logd
import sk.uxtweak.uxmobile.core.logi
import sk.uxtweak.uxmobile.core.logw
import sk.uxtweak.uxmobile.core.toCurrentList
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.net.EmitException
import sk.uxtweak.uxmobile.util.toJson
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class EventSender(private val connector: ConnectionManager) {
    private val channel = Channel<SessionEvent>(CHANNEL_CAPACITY)
    private lateinit var loopJob: Job

    fun start(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        loop()
    }

    fun cancel() = loopJob.cancel()

    suspend fun cancelAndJoin() = loopJob.cancelAndJoin()

    fun offer(event: SessionEvent) = channel.offer(event)

    suspend fun send(event: SessionEvent) = channel.send(event)

    suspend fun sendAll(events: List<SessionEvent>) = events.forEach { channel.send(it) }

    suspend fun loop() {
        connector.waitUntilConnected()
        supervisorScope {
            logi(
                TAG,
                "Launched sender scope"
            )
            launch(Dispatchers.IO) {
                while (isActive) {
                    logi(
                        TAG,
                        "Throttling..."
                    )
                    delay(THROTTLE_DELAY)
                    val events = channel.toCurrentList()
                    if (events.isNotEmpty()) {
                        logd(
                            TAG,
                            "Sending next batch of events (${events.size})"
                        )
                        try {
                            connector.emit(EVENTS_CHANNEL_NAME, Event.EventsList(events).toJson())
                        } catch (exception: IOException) {
                            logi(
                                TAG,
                                "Cannot reach WebSocket event server"
                            )
                            launch { sendAll(events) }
                            connector.waitUntilConnected()
                        } catch (exception: EmitException) {
                            logw(
                                TAG,
                                "Server responded with error: $exception"
                            )
                            launch { sendAll(events) }
                        } catch (exception: Exception) {
                            logw(
                                TAG,
                                "Received exception while emitting event: $exception"
                            )
                            launch { sendAll(events) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "UxMobile"
        private const val EVENTS_CHANNEL_NAME = "events"
        private const val THROTTLE_DELAY = 1000L
        private const val CHANNEL_CAPACITY = 100
    }
}
