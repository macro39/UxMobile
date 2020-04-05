package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.persister.room.AppDatabase
import sk.uxtweak.uxmobile.persister.room.EventEntity
import sk.uxtweak.uxmobile.persister.room.SessionEntity
import sk.uxtweak.uxmobile.recorder.events.EventRecorder
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame
import sk.uxtweak.uxmobile.recorder.screen.ScreenRecorder
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.util.*

@OptIn(ExperimentalStdlibApi::class)
class Persister(
    private val recorder: EventRecorder,
    private val screenRecorder: ScreenRecorder,
    private val database: AppDatabase
) {
    private val chunkMuxer = ChunkMuxer(keyFramesInOneChunk = 2)
    private val localQueue = LocalQueue<Event>(100)
    private val tempList = mutableListOf<Event>()
    private var sessionId: Long = -1

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
        sessionId = database.sessionDao().insert(SessionEntity())

        logd(TAG, "Session ID ($sessionId) generated, sending temporary list (${tempList.size}) to LQ")
        tempList.forEach { localQueue.insert(it) }
        tempList.clear()

        logd(TAG, "Starting video chunk muxer")
        chunkMuxer.filesPath = IOUtils.filesDir.path
        if (!chunkMuxer.isRunning) {
            chunkMuxer.start()
        }
    }

    private fun onEventReceived(event: Event) {
        if (sessionId == -1L) {
            logd(TAG, "Storing event to temporary list")
            tempList += event
        } else {
            logd(TAG, "Sending event to local queue")
            localQueue.insert(event)
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

    private suspend fun onLocalQueueLimitReached(queue: ArrayDeque<Event>) {
        logd(TAG, "Local queue limit reached, storing events into database")
        val events = queue.map { EventEntity(0, sessionId, it.toJson()) }
        database.eventDao().insert(events)
        queue.clear()
        GlobalScope.launch {
            logd(TAG, "Events in database ${database.eventDao().count()}")
        }
    }
}
