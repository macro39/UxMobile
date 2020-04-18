package sk.uxtweak.uxmobile.sender

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import sk.uxtweak.uxmobile.concurrency.IOContext
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.persister.Persister
import sk.uxtweak.uxmobile.persister.database.AppDatabase
import sk.uxtweak.uxmobile.persister.database.EventEntity
import sk.uxtweak.uxmobile.persister.database.VideoEntity
import sk.uxtweak.uxmobile.util.*
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext

class EventSender(
    private val sessionManager: SessionManager,
    private val connection: ConnectionManager,
    private val persister: Persister,
    private val database: AppDatabase
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = reusableContext
    private lateinit var reusableContext: CoroutineContext

    var isRunning: Boolean = false
        private set

    private var changedSendJob: Job? = null

    private lateinit var videoLiveData: LiveData<List<VideoEntity>>
    private lateinit var eventsLiveData: LiveData<List<EventEntity>>

    private val eventsChangedObserver = Observer<List<EventEntity>> { events ->
        launch {
            connection.emit(EVENT_CHANNEL, events.toJson())
            database.eventDao().delete(events)
        }
    }

    private val videoChangedObserver = Observer<List<VideoEntity>> { videos ->
        launch {
            changedSendJob?.join()
            changedSendJob = launch {
                videos.forEach {
                    val file = File(it.path)
                    if (file.exists()) {
                        val sessionId = database.recordingDao().getById(it.recordingId).sessionId
                        logd(TAG, "Sending file ${it.recordingId} with session ID $sessionId")
                        sendFile(
                            file,
                            it.recordingId.toString(),
                            sessionId
                        )
                        file.delete()
                    } else {
                        logw(TAG, "File ${file.path} does not exists!")
                    }
                    database.videoDao().delete(it)
                }
            }
        }
    }

    fun start() {
        reusableContext = IOContext()
        isRunning = true
        connection.setOnConnected(::onConnected)
        connection.setOnDisconnected(::onDisconnected)
        persister.doOnEventsFlush(this, ::onEventsFlush)
    }

    fun stop() {
        cancel()
        persister.clearFlushListener()
        connection.setOnConnected {}
        connection.setOnDisconnected {}
        isRunning = false
    }

    private fun onConnected() {
        videoLiveData = database.videoDao().getAll()
        videoLiveData.observeForever(videoChangedObserver)
        changedSendJob = connection.launch {
            while (isActive) {
                try {
                    logd(TAG, "Trying to send events and screen from DB")
                    sendStoredEvents()
                    logd(TAG, "Sent all events and screen from DB")
                    break
                } catch (exception: Exception) {
                    logw(TAG, "Could not send events or screen (${exception.message})")
                    delay(RETRY_ATTEMPT_DELAY)
                }
            }
        }
        if (persister.isRunning) {
            eventsLiveData = database.eventDao().getForRecordingLive(persister.recordingId)
            eventsLiveData.observeForever(eventsChangedObserver)
        }
    }

    private fun onDisconnected() {
        videoLiveData.removeObserver(videoChangedObserver)
        eventsLiveData.removeObserver(eventsChangedObserver)
    }

    private suspend fun sendStoredEvents() {
        val recordings = if (persister.isRunning) {
            database.recordingDao().getAllExcept(persister.recordingId)
        } else {
            database.recordingDao().getAll()
        }

        recordings.forEach {
            val events = database.eventDao().getForRecording(it.id)
            if (events.isNotEmpty()) {
                connection.emit(EVENT_CHANNEL, events.toJson())
            }
            database.recordingDao().delete(it)
        }
    }

    private suspend fun onEventsFlush(events: List<Event>): Boolean {
        if (!connection.isConnected) {
            return false
        }
        return try {
            connection.emit(
                EVENT_CHANNEL,
                Event.EventsList(persister.recordingId, sessionManager.sessionId, events).toJson()
            )
            logd(TAG, "Sent events to server")
            true
        } catch (exception: Exception) {
            logd(TAG, "Could not send events to server, storing to database")
            false
        }
    }

    private suspend fun sendFile(file: File, recordingId: String, sessionId: String) {
        val data = FileInputStream(file).use { it.readBytes() }
        val encoded = Base64.encodeToString(data, Base64.DEFAULT)
        val event = Event.VideoChunkEvent(file.nameWithoutExtension, encoded)
        val sessionEvent = SessionEvent(recordingId, sessionId, event)
        connection.emit(EVENT_CHANNEL, sessionEvent.toJson())
    }

    private fun List<EventEntity>.toJson(): String {
        val array = buildJsonArray {
            for (event in this@toJson) {
                put(JSONObject(event.json))
            }
        }
        return buildJsonObject {
            put("events", array)
        }
    }

    private fun buildJsonArray(action: JSONArray.() -> Unit) = JSONArray().apply {
        action(this)
    }.toString()

    private fun buildJsonObject(action: JSONObject.() -> Unit) = JSONObject().apply {
        action(this)
    }.toString()

    companion object {
        private const val RETRY_ATTEMPT_DELAY = 5000L
        private const val EVENT_CHANNEL = "events"
    }
}
