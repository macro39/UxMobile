package sk.uxtweak.uxmobile.sender

import android.util.Base64
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import sk.uxtweak.uxmobile.UxMobile
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.core.toHumanUnit
import sk.uxtweak.uxmobile.core.withFixedDelay
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.persister.Persister
import sk.uxtweak.uxmobile.persister.database.AppDatabase
import sk.uxtweak.uxmobile.persister.database.EventEntity
import sk.uxtweak.uxmobile.util.*
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeoutException
import kotlin.math.max

class EventSender(
    private val sessionManager: SessionManager,
    private val connection: ConnectionManager,
    private val persister: Persister,
    private val database: AppDatabase
) {
    var isRunning: Boolean = false
        private set

    private lateinit var job: Job

    private val senderJob: suspend CoroutineScope.() -> Unit = {
        connection.waitUntilConnected()
        sendNextVideoSession()
        sendNextSession()
    }

    fun start() {
        isRunning = true
        job = GlobalScope.withFixedDelay(Dispatchers.IO, SENDER_JOB_DELAY, senderJob)
    }

    fun stop() = runBlocking {
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        job.cancelAndJoin()
        isRunning = false
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun sendLocalQueue() {
        persister.fetchEvents {
            logd(TAG, "Sending events in memory (${it.size})")
            connection.emit(EVENT_CHANNEL, Event.EventsList(it.toList()).toJson())
            it.clear()
        }
    }

    private suspend fun sendNextVideoSession() {
        val sessionDirectories = IOUtils.filesDir.listFiles()!!

        deleteEmptyDirectories(sessionDirectories)
        val directory = getFirstDirectory(sessionDirectories) ?: return

        val isSessionDirectory = sessionManager.sessionId == directory.name
        val isStoringVideo = UxMobile.sessionManager.persister.isRunning &&
            UxMobile.sessionManager.screenRecorder.isRunning

        val files = directory.listFiles()
        files?.sortedBy { it.name }
            ?.take(if (isSessionDirectory && isStoringVideo) max(files.size - 1, 0) else files.size)
            ?.forEach { file ->
                val data = FileInputStream(file).use {
                    it.readBytes()
                }
                logd(TAG, "Sending file ${file.name} (${data.size.toHumanUnit()})")
                val encodedData = Base64.encodeToString(data, Base64.DEFAULT)
                val event = Event.VideoChunkEvent(file.nameWithoutExtension, encodedData)
                val sessionEvent = SessionEvent(directory.name, 0, event)
                val eventsList = Event.EventsList(listOf(sessionEvent))
                try {
                    connection.emit(EVENT_CHANNEL, eventsList.toJson())
                    file.delete()
                } catch (exception: TimeoutException) {
                    logw(TAG, "Timeout when sending video chunk")
                } catch (exception: Exception) {
                    logw(TAG, "Cannot send video chunk ${file.name} in session ${directory.name}")
                }
            }
    }

    private suspend fun sendNextSession() {
        val session = database.sessionDao().first()
        if (session == null) {
            sendLocalQueue()
            return
        }

        val events = database.eventDao().getForSessionId(session.uuid)
        logd(TAG, "Sending session ${session.uuid} (Events: ${events.size})")
        try {
            for (chunk in events.chunked(100)) {
                logd(TAG, "Sending chunk")
                connection.emit(EVENT_CHANNEL, chunk.toJson())
                database.eventDao().deleteEvents(chunk)
            }
            if (session.uuid != sessionManager.sessionId) {
                database.sessionDao().delete(session)
            }

            logd(TAG, "Sessions from database sent, sending events from memory")
            sendLocalQueue()
        } catch (exception: Exception) {
            logw(TAG, "Cannot send events", exception)
        }
    }

    private fun List<EventEntity>.toJson(): String {
        val array = JSONArray()
        for (event in this) {
            array.put(JSONObject(event.json))
        }
        val obj = JSONObject()
        obj.put("events", array)
        return obj.toString()
    }

    private fun deleteEmptyDirectories(sessionDirectories: Array<File>) {
        val shouldDeleteSessionDirectory = !UxMobile.sessionManager.persister.isRunning ||
            !UxMobile.sessionManager.screenRecorder.isRunning
        sessionDirectories.filter {
            it.listFiles()?.isEmpty() ?: true && (sessionManager.sessionId != it.name || shouldDeleteSessionDirectory)
        }.forEach { it.delete() }
    }

    private fun getFirstDirectory(sessionDirectories: Array<File>): File? {
        val possibleDirectories = sessionDirectories.filter {
            it.listFiles()?.isNotEmpty() ?: false && it.name != sessionManager.sessionId
        }
        return if (possibleDirectories.isNotEmpty()) {
            possibleDirectories.first()
        } else {
            sessionDirectories.find { it.name == sessionManager.sessionId }
        }
    }

    companion object {
        private const val EVENT_CHANNEL = "events"
        private const val SENDER_JOB_DELAY = 1000L
    }
}
