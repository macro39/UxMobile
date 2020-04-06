package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import android.os.SystemClock
import com.fasterxml.uuid.Generators
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.persister.room.AppDatabase
import sk.uxtweak.uxmobile.persister.room.EventEntity
import sk.uxtweak.uxmobile.persister.room.SessionEntity
import sk.uxtweak.uxmobile.recorder.events.EventRecorder
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame
import sk.uxtweak.uxmobile.recorder.screen.ScreenRecorder
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.util.*
import java.io.File

@OptIn(ExperimentalStdlibApi::class)
class Persister(
    private val recorder: EventRecorder,
    private val screenRecorder: ScreenRecorder,
    private val database: AppDatabase
) {
    private val chunkMuxer = ChunkMuxer(keyFramesInOneChunk = 2)
    private val tempList = mutableListOf<SessionEvent>()
    val localQueue = LocalQueue<SessionEvent>(100)
    var sessionId: String? = null

    fun start() {
        logi(TAG, "Starting persister")
        localQueue.start()
        recorder.addOnEventListener(::onEventReceived)
        screenRecorder.setOnEncodedFrameListener(::onEncodedFrame)
        screenRecorder.setOnOutputFormatChangedListener(::onOutputFormatChanged)
        localQueue.doOnLimitReached(::onLocalQueueLimitReached)
    }

    fun stop() {
        logi(TAG, "Stopping persister")
        screenRecorder.setOnEncodedFrameListener {}
        screenRecorder.setOnOutputFormatChangedListener {}
        recorder.removeOnEventListener(::onEventReceived)
        localQueue.stop()
        chunkMuxer.stop()
    }

    fun generateNewSessionId(scope: CoroutineScope = GlobalScope) = scope.launch(Dispatchers.IO) {
        logd(TAG, "Generating session ID")
        sessionId = Generators.timeBasedGenerator().generate().toString()
        database.sessionDao().insert(SessionEntity(sessionId!!))

        logd(TAG, "Session ID ($sessionId) generated, sending temporary list (${tempList.size}) to LQ")
        tempList.forEach { it.sessionId = sessionId }
        tempList.forEach { localQueue.insert(it) }
        tempList.clear()

        logd(TAG, "Starting video chunk muxer")
        val sessionDirectory = File(IOUtils.filesDir, sessionId.toString())
        if (!sessionDirectory.mkdirs()) {
            logw(TAG, "Cannot create session directory ${sessionDirectory.path}")
        }
        chunkMuxer.filesPath = sessionDirectory.path
        if (!chunkMuxer.isRunning) {
            chunkMuxer.start()
        }
    }

    suspend fun flushEvents() {
        localQueue.withLock {
            insertEventsIntoDatabase(it)
        }
    }

    private fun onEventReceived(event: Event) {
        val sessionEvent = SessionEvent(null, SystemClock.elapsedRealtime(), event)
        if (sessionId == null) {
            logd(TAG, "Storing event to temporary list")
            tempList += sessionEvent
        } else {
            logd(TAG, "Sending event to local queue")
            sessionEvent.sessionId = sessionId.toString()
            localQueue.insert(sessionEvent)
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

    private suspend fun onLocalQueueLimitReached(queue: ArrayDeque<SessionEvent>) {
        logd(TAG, "Local queue limit reached, storing events into database")
        insertEventsIntoDatabase(queue)
        GlobalScope.launch {
            logd(TAG, "Events in database ${database.eventDao().countForId(sessionId!!)}")
        }
    }

    private suspend fun insertEventsIntoDatabase(queue: ArrayDeque<SessionEvent>) {
        val events = queue.map { EventEntity(0, sessionId!!, it.toJson()) }
        database.eventDao().insert(events)
        queue.clear()
    }
}
