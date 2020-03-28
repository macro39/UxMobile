package sk.uxtweak.uxmobile.recorder.screen

import android.app.Activity
import android.media.MediaFormat
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.atFixedRate
import sk.uxtweak.uxmobile.core.logw
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity
import sk.uxtweak.uxmobile.persister.ChunkMuxer
import sk.uxtweak.uxmobile.persister.MuxerCommand
import sk.uxtweak.uxmobile.persister.stopAndJoin

class ScreenRecorder(filesPath: String, private val videoFormat: VideoFormat) {
    private val encoder = VideoEncoder(videoFormat)
    private val screenBuffer = ScreenBuffer(videoFormat.width, videoFormat.height)
    private val muxer = ChunkMuxer(filesPath, 2)
    private lateinit var job: Job
    private var onChunkReadyListener: (String) -> Unit = {}

    private val recordingJob: suspend CoroutineScope.() -> Unit = {
        while (isActive) {
            ForegroundActivityHolder.withForegroundActivity {
                drawFrame(it)
                encodeFrame()
            }
        }
    }

    init {
        encoder.setOnEncodedListener(::onEncodedFrame)
        encoder.setOnOutputFormatChanged(::onOutputFormatChanged)
    }

    fun start() {
        job = GlobalScope.atFixedRate(
            Dispatchers.IO,
            block = recordingJob,
            rate = videoFormat.frameTime
        )
        encoder.start()
        muxer.start()
    }

    fun stop() {
        runBlocking { job.cancelAndJoin() }
        encoder.stop()
        muxer.stopAndJoin()
        encoder.release()
    }

    fun setOnChunkReady(listener: (String) -> Unit) {
        onChunkReadyListener = listener
    }

    private fun onOutputFormatChanged(format: MediaFormat) {
        muxer.postCommand(MuxerCommand.ChangeOutputFormat(format))
    }

    private fun onEncodedFrame(frame: EncodedFrame) {
        Stats.onFrameEncoded(frame)
        muxer.postCommand(MuxerCommand.MuxFrame(frame))
    }

    private suspend fun drawFrame(activity: Activity) {
        val rootLayout = activity.window.decorView
        if (rootLayout.width == 0 && rootLayout.height == 0) {
            return
        }
        screenBuffer.drawToBuffer(rootLayout)
    }

    private fun encodeFrame() {
        if (!screenBuffer.isEmpty) {
            encoder.encode(screenBuffer.bitmap)
        } else {
            logw(
                TAG,
                "Screen buffer is empty while encoding!"
            )
        }
    }

    companion object {
        private const val TAG = "UxMobile"
    }
}
