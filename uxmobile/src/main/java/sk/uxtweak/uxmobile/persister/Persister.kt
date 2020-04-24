package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import android.os.SystemClock
import androidx.annotation.MainThread
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.concurrency.MainContext
import sk.uxtweak.uxmobile.concurrency.requireMainThread
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.persister.database.AppDatabase
import sk.uxtweak.uxmobile.persister.database.EventEntity
import sk.uxtweak.uxmobile.persister.database.RecordingEntity
import sk.uxtweak.uxmobile.persister.database.VideoEntity
import sk.uxtweak.uxmobile.recorder.events.EventRecorder
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame
import sk.uxtweak.uxmobile.recorder.screen.ScreenRecorder
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.util.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class Persister(
    private val sessionManager: SessionManager,
    private val eventRecorder: EventRecorder,
    private val screenRecorder: ScreenRecorder,
    private val database: AppDatabase
): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = reusableContext
    private lateinit var reusableContext: CoroutineContext

    var isRunning = false
        private set

    private var startTime = 0L
    var recordingId = 0L
    var studyId: Int? = null
    private val chunkMuxer = ChunkMuxer(keyFramesInOneChunk = 2)
    private val events = ArrayList<Event>(EVENT_LOCAL_LIMIT)
    private var onEventsFlushedListener: suspend (ArrayList<Event>) -> Boolean = { false }
    private var onFileMuxedListener: suspend (File, Boolean) -> Boolean = { _, _ -> false }

    val eventsCount: Int
        get() = events.size

    init {
        eventRecorder.addOnEventListener(::onEventReceived)
        screenRecorder.setOnFirstFrameDrawListener(::onFirstFrameDraw)
        screenRecorder.setOnOutputFormatChangedListener(::onOutputFormatChanged)
        screenRecorder.setOnEncodedFrameListener(::onEncodedFrame)
        chunkMuxer.doOnFileMuxed(::onFileMuxed)
    }

    fun start(studyId: Int? = null) {
        logi(TAG, "Starting persister")
        reusableContext = MainContext()
        isRunning = true
        this.studyId = studyId
        startTime = SystemClock.elapsedRealtime()
        insertEvent(Event.StartEvent)
        eventRecorder.start()
        launch(Dispatchers.IO + NonCancellable) {
            recordingId = database.recordingDao().insert(RecordingEntity(0, sessionManager.sessionId, studyId))
            startChunkMuxer()
            screenRecorder.start()
            deleteEmptyDirectories()
        }
    }

    fun stop() {
        logi(TAG, "Stopping persister")
        screenRecorder.stop()
        eventRecorder.stop()
        insertEvent(Event.EndEvent)
        flush()
        chunkMuxer.stopAndJoin()
        cancel()
        isRunning = false
    }

    @MainThread
    fun flushEvents() {
        requireMainThread()
        flush()
    }

    fun persist(eventList: List<Event>) {
        val eventsEntity = eventList.map { EventEntity(0, recordingId.toString(), it.toJson()) }
        launch(Dispatchers.IO + NonCancellable) {
            database.eventDao().insert(eventsEntity)
        }
    }

    fun doOnEventsFlush(listener: suspend (ArrayList<Event>) -> Boolean) {
        onEventsFlushedListener = listener
    }

    fun clearFlushListener() {
        onEventsFlushedListener = { false }
    }

    fun doOnFileMuxed(listener: suspend (File, Boolean) -> Boolean) {
        onFileMuxedListener = listener
    }

    fun clearFileMuxedListener() {
        onFileMuxedListener = { _, _ -> false }
    }

    private fun onEventReceived(event: Event) {
        insertEvent(event)

        if (events.size >= EVENT_LOCAL_LIMIT) {
            flush()
        }
    }

    @MainThread
    private fun flush() {
        logd(TAG, "Flushing events")
        val eventsCopy = ArrayList(events)
        events.clear()
        launch(Dispatchers.IO + NonCancellable) {
            if (!onEventsFlushedListener(eventsCopy)) {
                logd(TAG, "Flushed events not sent, storing into database")
                persist(eventsCopy)
                logd(TAG, "Flushed events stored into database")
            } else {
                logd(TAG, "Flushed events send to server")
            }
        }
    }

    private fun onFirstFrameDraw() {
        logd(TAG, "Before first frame is drawn")
        insertEvent(Event.VideoStartEvent)
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

    private fun onFileMuxed(muxedFile: File, isLast: Boolean) {
        launch(Dispatchers.IO + NonCancellable) {
            if (!onFileMuxedListener(muxedFile, isLast)) {
                logd(TAG, "File ${muxedFile.path} muxed and not sent, inserting into database")
                database.videoDao().insert(VideoEntity(
                    recordingId = recordingId,
                    chunkId = muxedFile.nameWithoutExtension.toInt())
                )
            } else {
                logd(TAG, "File ${muxedFile.path} muxed and sent to server")
                muxedFile.delete()
            }
        }
    }

    suspend fun fetchDatabaseStats(): List<String> {
        val recordings = mutableMapOf<String, Pair<String, Int>>()
        database.eventDao().getAll().forEach {
            val eventCount = (recordings[it.recordingId]?.second ?: 0) + 1
            val sessionId = database.recordingDao().getById(it.recordingId.toLong()).sessionId
            recordings[it.recordingId] = sessionId to eventCount
        }
        return recordings.toSortedMap(Collections.reverseOrder()).map { "${it.key} (${it.value.first} Events: ${it.value.second})" }
    }

    private fun startChunkMuxer() {
        chunkMuxer.filesPath = File(IOUtils.filesDir, recordingId.toString())
        logd(TAG, "Starting video chunk muxer")
        chunkMuxer.start()
    }

    private fun deleteEmptyDirectories() {
        IOUtils.filesDir.listFiles()
            ?.filter { it.name != recordingId.toString() && it.isDirectory && it.list().isNullOrEmpty() }
            ?.forEach {
                logd(TAG, "Deleting empty directory: ${it.name}")
                it.delete()
            }
    }

    private fun insertEvent(event: Event) {
        event.at -= startTime
        events += event
    }

    companion object {
        private const val EVENT_LOCAL_LIMIT = 100
    }
}
