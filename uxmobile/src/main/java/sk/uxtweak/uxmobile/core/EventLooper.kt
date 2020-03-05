package sk.uxtweak.uxmobile.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import sk.uxtweak.uxmobile.logd
import sk.uxtweak.uxmobile.logi
import sk.uxtweak.uxmobile.logw
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.EmitException
import sk.uxtweak.uxmobile.util.toJson
import java.io.IOException
import java.util.concurrent.Executors

/**
 * Looper that send all events in its queue. To add event to queue to be sent later, use [send].
 * To start looper, call [loop] method. Looper will wait for socket connection before it starts
 * sending events. It does not handle socket connection to host. An example on how to setup
 * the event looper for whole application lifecycle:
 *
 *     val looper = EventLooper(socket)
 *     looper.startGlobally()
 *     looper.send(sessionEvent)
 *
 * @param connector connector that handles connection to server
 */
class EventLooper(private val connector: ConnectionManager) {
    private val channel = Channel<SessionEvent>(Channel.UNLIMITED)
    private val dispatcher = Executors.newFixedThreadPool(NO_THREADS).asCoroutineDispatcher()

    init {
        logi(TAG, "Initializing EventLooper")
    }

    /**
     * Adds event to queue to be sent later and returns immediately.
     *
     * @param event event that will be added to queue
     * @return whether event was added successfully
     */
    fun offer(event: SessionEvent): Boolean {
        logd(TAG, "Offering event to queue: $event")
        return channel.offer(event)
    }

    /**
     * Adds event to queue to be sent later. Suspends until event is in queue.
     *
     * @param event event that will be added to queue
     */
    suspend fun send(event: SessionEvent) {
        logd(TAG, "Sending event to queue: $event")
        channel.send(event)
    }

    /**
     * Main looper method. Call to start sending queued events to server. Will suspend until
     * channel for events is closed.
     */
    suspend fun loop() {
        logi(TAG, "Starting loop")
        connector.waitUntilConnected()
        supervisorScope {
            logi(TAG, "Launched supervisor scope")
            for (event in channel) {
                // Using custom dispatcher because Default would starve and IO would create too many
                // threads
                launch(dispatcher) {
                    try {
                        logd(TAG, "Sending event to server: $event")
                        connector.emit(EVENTS_CHANNEL_NAME, event.toJson())
                    } catch (exception: IOException) {
                        logi(TAG, "Cannot reach WebSocket event server")
                        connector.waitUntilConnected()
                        send(event)
                    } catch (exception: EmitException) {
                        logw(TAG, "Server responded with error: $exception")
                        send(event)
                        // TODO: Received error from server
                    } catch (exception: Exception) {
                        logw(TAG, "Received exception while emitting event: $exception")
                        send(event)
                        // TODO: Other exception
                    }
                }
            }
        }
    }

    /**
     * Retrieves and removes all queued events that are scheduled to be sent and returns them.
     *
     * @return all queued events that has not been sent yet
     */
    suspend fun pollQueuedEvents(): List<SessionEvent> = mutableListOf<SessionEvent>().apply {
        for (event in channel) {
            this += event
        }
        logd(TAG, "Polling all remaining $size events from queue")
    }

    companion object {
        private const val EVENTS_CHANNEL_NAME = "events"
        private val NO_THREADS = Runtime.getRuntime().availableProcessors()
        private val TAG = EventLooper::class.java.simpleName
    }
}

fun EventLooper.startGlobally() {
    GlobalScope.launch(Dispatchers.IO) { loop() }
}
