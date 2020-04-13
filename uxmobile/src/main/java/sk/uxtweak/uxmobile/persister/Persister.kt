package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import android.os.SystemClock
import androidx.annotation.WorkerThread
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sk.uxtweak.uxmobile.concurrency.withTryLock
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.persister.database.AppDatabase
import sk.uxtweak.uxmobile.persister.database.EventEntity
import sk.uxtweak.uxmobile.persister.database.SessionEntity
import sk.uxtweak.uxmobile.recorder.events.EventRecorder
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame
import sk.uxtweak.uxmobile.recorder.screen.ScreenRecorder
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.recorder.screen.newSingleCoroutineDispatcher
import sk.uxtweak.uxmobile.util.*
import java.io.File

class DatabaseSession(private val sessionId: String, private val eventCount: Int) {
    override fun toString() = "$sessionId (Events: $eventCount)"
}

@OptIn(ExperimentalStdlibApi::class)
class Persister(
    private val sessionManager: SessionManager,
    private val eventRecorder: EventRecorder,
    private val screenRecorder: ScreenRecorder,
    private val database: AppDatabase
) {
    var isRunning: Boolean = false
        private set

    private val chunkMuxer = ChunkMuxer(keyFramesInOneChunk = 2)
    private val databaseJobs = mutableListOf<Job>()
    private val databaseContext = newSingleCoroutineDispatcher("Database writer")

    private val mutex = Mutex()
    private val events = ArrayDeque<SessionEvent>(EVENT_LOCAL_LIMIT)
    private val cache = ArrayDeque<SessionEvent>(EVENT_LOCAL_LIMIT)

    val cachedEventsCount: Int
        get() = cache.size

    val eventsCount: Int
        get() = events.size

    init {
        eventRecorder.addOnEventListener(::onEventReceived)
        screenRecorder.setOnOutputFormatChangedListener(::onOutputFormatChanged)
        screenRecorder.setOnEncodedFrameListener(::onEncodedFrame)
    }

    fun start() {
        logi(TAG, "Starting persister")
        isRunning = true
        GlobalScope.launch(Dispatchers.IO) {
            database.sessionDao().insert(SessionEntity(sessionManager.sessionId))
        }
        startChunkMuxer()
        eventRecorder.start()
        screenRecorder.start()
    }

    fun stop() {
        logi(TAG, "Stopping persister")
        screenRecorder.stop()
        eventRecorder.stop()
        flushEvents()
        waitForDatabaseJobs()
        chunkMuxer.stopAndJoin()
        isRunning = false
    }

    private fun waitForDatabaseJobs() {
        runBlocking { databaseJobs.forEach { it.join() } }
        databaseJobs.clear()
    }

    fun flushEvents() {
        databaseJobs.removeAll { it.isCompleted }
        if (events.isNotEmpty()) {
            val eventsToFlush = events.map { EventEntity(0, sessionManager.sessionId, it.toJson()) }
            events.clear()
            databaseJobs += GlobalScope.launch(databaseContext) {
                logd(TAG, "Inserting events (${eventsToFlush.size}) into database")
                mutex.withLock {
                    database.eventDao().insert(eventsToFlush)
                }
            }
        }
    }

    private fun onEventReceived(event: Event) {
        logd(TAG, "Sending event to local queue")
        val sessionEvent = SessionEvent(sessionManager.sessionId, SystemClock.elapsedRealtime(), event)

        mutex.withTryLock({
            events.addAll(cache)
            cache.clear()

            events.add(sessionEvent)
            if (events.size >= EVENT_LOCAL_LIMIT) {
                flushEvents()
            }
        }, {
            cache.add(sessionEvent)
        })
    }

    private fun onEncodedFrame(frame: EncodedFrame) {
        if (frame.isKeyFrame) {
            logd(TAG, "Key frame encoded (${frame.bufferInfo.size})")
        }
        chunkMuxer.postCommand(MuxerCommand.MuxFrame(frame))
    }

    private fun onOutputFormatChanged(format: MediaFormat) {
        logd(TAG, "Output format changed")
        chunkMuxer.postCommand(MuxerCommand.ChangeOutputFormat(format))
    }

    suspend fun fetchDatabaseStats(): List<DatabaseSession> {
        val sessions = mutableMapOf<String, Int>()
        database.eventDao().getAll().forEach {
            sessions[it.sessionId] = (sessions[it.sessionId] ?: 0) + 1
        }
        return sessions.map { DatabaseSession(it.key, it.value) }
    }

    private fun startChunkMuxer() {
        chunkMuxer.filesPath = File(IOUtils.filesDir, sessionManager.sessionId)
        logd(TAG, "Starting video chunk muxer")
        chunkMuxer.start()
    }

    suspend fun fetchEvents(action: suspend (ArrayDeque<SessionEvent>) -> Unit) {
        if (events.isNotEmpty()) {
            mutex.withLock {
                action(events)
            }
        }
    }

    companion object {
        private const val EVENT_LOCAL_LIMIT = 100
    }
}
