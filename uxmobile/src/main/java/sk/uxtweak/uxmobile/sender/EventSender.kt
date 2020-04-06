package sk.uxtweak.uxmobile.sender

import android.util.Base64
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import sk.uxtweak.uxmobile.core.withFixedDelay
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.persister.Persister
import sk.uxtweak.uxmobile.persister.room.AppDatabase
import sk.uxtweak.uxmobile.util.*
import java.io.FileInputStream
import java.util.concurrent.TimeoutException

class EventSender(
    private val connection: ConnectionManager,
    private val persister: Persister,
    private val database: AppDatabase
) {
    private lateinit var job: Job

    private val senderJob: suspend CoroutineScope.() -> Unit = {
        connection.suspendUntilConnected()
        sendNextVideoSession()
        sendNextSession()
    }

    fun start() {
        job = GlobalScope.withFixedDelay(Dispatchers.IO, 1000, senderJob)
    }

    fun stop(scope: CoroutineScope = GlobalScope) = scope.launch(Dispatchers.IO) {
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        job.cancelAndJoin()
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun sendLocalQueue() {
        persister.localQueue.withLock { queue ->
            if (queue.size > 0) {
                logd(TAG, "Sending events in memory (${queue.size})")
                connection.emit("events", Event.EventsList(queue.toList()).toJson())
                queue.clear()
            } else {
                logd(TAG, "No events in memory")
            }
        }
    }

    private suspend fun sendNextVideoSession() {
        val sessionDirectories = IOUtils.filesDir.listFiles()!!
        logd(TAG, "Session directories: ${sessionDirectories.size}")

        sessionDirectories.filter {
            it.listFiles()?.isEmpty() ?: true && persister.sessionId != it.name
        }.forEach { it.delete() }
        val directory =
            sessionDirectories.filterNot { it.listFiles()?.isEmpty() ?: true }.minBy { it.name }
                ?: return

        val sessionDirectory = persister.sessionId.toString() == directory.name

        logd(TAG, "Session directory: ${directory.name} ($sessionDirectory)")
        val files = directory.listFiles()!!
        files.sortedBy { it.name }
            .take(if (sessionDirectory) files.size - 1 else files.size)
            .forEach {
                try {
                    logd(TAG, "Sending file ${it.name}")
                    val data = FileInputStream(it).readBytes()
                    val encodedData = Base64.encodeToString(data, Base64.DEFAULT)
                    val event = Event.VideoChunkEvent(it.nameWithoutExtension, encodedData)
                    val sessionEvent = SessionEvent(directory.name, 0, event)
                    val eventsList = Event.EventsList(listOf(sessionEvent))
                    connection.emit("events", eventsList.toJson())
                    it.delete()
                } catch (exception: TimeoutException) {
                    logw(TAG, "Timeout when sending video chunk")
                }
            }
    }

    private suspend fun sendNextSession() {
        val session = database.sessionDao().first()
        if (session == null) {
            logd(TAG, "No session in database")
            sendLocalQueue()
            return
        }

        val events = database.eventDao().getForSessionId(session.uuid)
        logd(TAG, "Sending session ${session.uuid} (Events: ${events.size})")
        try {
            for (chunk in events.chunked(100)) {
                logd(TAG, "Sending chunk")
                val array = JSONArray()
                chunk.forEach { array.put(JSONObject(it.json)) }
                val obj = JSONObject()
                obj.put("events", array)
                connection.emit("events", obj.toString())
                database.eventDao().deleteEvents(chunk)
                logd(TAG, "Chunk sent and deleted")
            }
            if (session.uuid != persister.sessionId) {
                database.sessionDao().delete(session)
            }

            logd(TAG, "Sessions from database sent, sending events from memory")
            sendLocalQueue()
        } catch (exception: Exception) {
            logw(TAG, "Cannot send events", exception)
        }
    }

    private suspend fun generateServerId() = connection.emit("generateSessionId", "").toString()
}
