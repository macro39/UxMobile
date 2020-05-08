package sk.uxtweak.uxmobile.sender

import android.util.Base64
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.concurrency.IOContext
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.core.toJson
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.EventsList
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.persister.Persister
import sk.uxtweak.uxmobile.persister.database.AppDatabase
import sk.uxtweak.uxmobile.persister.database.VideoEntity
import sk.uxtweak.uxmobile.util.*
import java.io.File
import java.io.FileInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

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

    fun start() {
        logd(TAG, "Starting sender")
        reusableContext = IOContext()
        isRunning = true
        connection.setOnConnected(::onConnected)
        persister.doOnEventsFlush(::onEventsFlush)
        persister.doOnFileMuxed(::onFileMuxed)
    }

    fun stop() {
        val time = measureTimeMillis {
            cancel()
        }
        logd(TAG, "Stopped sender (cancelling scope took $time ms)")
        persister.clearFileMuxedListener()
        persister.clearFlushListener()
        connection.setOnConnected {}
        isRunning = false
    }

    private fun onConnected() {
        launch(Dispatchers.IO + NonCancellable) {
            val videos = database.videoDao().getAll()
            logd(TAG, "Sending all stored videos (${videos.size})")

            val videoRecordings = mutableMapOf<Long, MutableList<VideoEntity>>()
            videos.forEach {
                if (videoRecordings[it.recordingId] == null) {
                    videoRecordings[it.recordingId] = mutableListOf()
                }
                videoRecordings[it.recordingId]!! += it
            }

            videoRecordings.forEach { recordings ->
                recordings.value.sortedBy { it.chunkId }.forEachIndexed { index, videoEntity ->
                    val video = File(IOUtils.filesDir, videoEntity.path)
                    if (video.exists()) {
                        val recording = database.recordingDao().getById(videoEntity.recordingId)
                        val isLast = persister.recordingId != videoEntity.recordingId && index == recordings.value.size - 1
                        logd(TAG, "Sending file ${video.path} (${videoEntity.recordingId} - ${recording.sessionId})")
                        sendFile(video, videoEntity.recordingId, recording.studyId, recording.sessionId, isLast)
                        logd(TAG, "File sent successfully, deleting from internal storage and database")
                        video.delete()
                        database.videoDao().delete(videoEntity)
                    } else {
                        logw(TAG, "Video file $video does not exists")
                    }
                }
            }
        }

        launch(Dispatchers.IO + NonCancellable) {
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
    }

    private suspend fun sendStoredEvents() {
        val recordings = database.recordingDao().getAll()

        logd(TAG, "Got ${recordings.size} recordings")
        recordings.forEach {
            val events = database.eventDao().getForRecording(it.id)
            logd(TAG, "Got ${events.size} for recording ${it.id} (${it.sessionId}) and study ${it.studyId}")
            if (events.isNotEmpty()) {
                val json = events.toJson(it.id.toString(), it.studyId, it.sessionId)
                connection.emit(EVENT_CHANNEL, json)
                logd(TAG, "Events sent, removing from database")
                database.eventDao().deleteEvents(events)
            }
            if (it.id != persister.recordingId && database.videoDao().getByRecordingId(it.id) == null) {
                logd(TAG, "No more events or videos for recording ${it.id}, removing from database")
                database.recordingDao().delete(it)
            }
        }
    }

    private suspend fun onEventsFlush(events: List<Event>): Boolean {
        if (!connection.isConnected) {
            return false
        }
        return try {
            logd(TAG, "Sending flushed events (${events.size})")
            connection.emit(
                EVENT_CHANNEL,
                EventsList(
                    persister.recordingId,
                    sessionManager.sessionId,
                    persister.studyId,
                    events
                ).toJson()
            )
            logd(TAG, "Sent flushed events to server")
            true
        } catch (exception: Exception) {
            logd(TAG, "Could not send events to server, storing to database")
            false
        }
    }

    private suspend fun onFileMuxed(video: File, isLast: Boolean): Boolean {
        if (!connection.isConnected) {
            return false
        }
        return try {
            sendFile(video, persister.recordingId, persister.studyId, sessionManager.sessionId, isLast)
            true
        } catch (exception: Exception) {
            logd(TAG, "Cannot send muxed file to server ($exception)")
            false
        }
    }

    private suspend fun sendFile(file: File, recordingId: Long, studyId: Int?, sessionId: String, isLast: Boolean) {
        val data = FileInputStream(file).use { it.readBytes() }
        val encoded = Base64.encodeToString(data, Base64.DEFAULT)
        val event = Event.VideoChunkEvent(file.nameWithoutExtension, isLast, encoded)
        val eventsList = EventsList(recordingId, sessionId, studyId, listOf(event))
        logd(TAG, "Sending file ${file.path} ${if (isLast) "(Last)" else ""}")
        connection.emit(EVENT_CHANNEL, eventsList.toJson())
    }

    companion object {
        private const val RETRY_ATTEMPT_DELAY = 5000L
        private const val EVENT_CHANNEL = "events"
    }
}
