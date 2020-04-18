package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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

    var isRunning: Boolean = false
        private set

    var recordingId = 0L
    private val chunkMuxer = ChunkMuxer(keyFramesInOneChunk = 2)
    private val events = ArrayList<Event>(EVENT_LOCAL_LIMIT)
    private var onEventsFlushedListener: suspend (ArrayList<Event>) -> Boolean = { false }
    private var listenerScope: CoroutineScope? = null

    val eventsCount: Int
        get() = events.size

    init {
        eventRecorder.addOnEventListener(::onEventReceived)
        screenRecorder.setOnOutputFormatChangedListener(::onOutputFormatChanged)
        screenRecorder.setOnEncodedFrameListener(::onEncodedFrame)
        chunkMuxer.doOnFileMuxed(::onFileMuxed)
    }

    fun start(studyId: String? = null) {
        logi(TAG, "Starting persister")
        reusableContext = MainContext()
        isRunning = true
        events += Event.StartEvent
        startChunkMuxer()
        eventRecorder.start()
        screenRecorder.start()
        launch {
            recordingId = database.recordingDao().insert(RecordingEntity(0, sessionManager.sessionId, studyId))
        }
    }

    fun stop() {
        logi(TAG, "Stopping persister")
        screenRecorder.stop()
        eventRecorder.stop()
        events += Event.EndEvent
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
        launch {
            database.eventDao().insert(eventsEntity)
        }
    }

    fun doOnEventsFlush(scope: CoroutineScope, listener: suspend (ArrayList<Event>) -> Boolean) {
        listenerScope = scope
        onEventsFlushedListener = listener
    }

    fun clearFlushListener() {
        listenerScope = null
        onEventsFlushedListener = { false }
    }

    private fun onEventReceived(event: Event) {
        logd(TAG, "Sending event to local queue")
        events += event

        if (events.size >= EVENT_LOCAL_LIMIT) {
            flush()
        }
    }

    @MainThread
    private fun flush() {
        val eventsCopy = ArrayList(events)
        events.clear()
        listenerScope?.launch {
            if (!onEventsFlushedListener(eventsCopy)) {
                persist(eventsCopy)
            }
        }
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

    private fun onFileMuxed(muxedFile: File) {
        database.videoDao().insert(VideoEntity(
            recordingId = recordingId,
            chunkId = muxedFile.nameWithoutExtension.toInt())
        )
    }

    suspend fun fetchDatabaseStats(): List<String> {
        val recordings = mutableMapOf<String, Pair<String, Int>>()
        database.eventDao().getAll().forEach {
            val eventCount = (recordings[it.recordingId]?.second ?: 0) + 1
            val sessionId = database.recordingDao().getById(it.recordingId.toLong()).sessionId
            recordings[it.recordingId] = sessionId to eventCount
        }
        return recordings.map { "${it.key} (${it.value.first} Events: ${it.value.second})" }
    }

    private fun startChunkMuxer() {
        chunkMuxer.filesPath = File(IOUtils.filesDir, recordingId.toString())
        logd(TAG, "Starting video chunk muxer")
        chunkMuxer.start()
    }

    companion object {
        private const val EVENT_LOCAL_LIMIT = 100
    }
}
