package sk.uxtweak.uxmobile.recorder.screen

import android.app.Activity
import android.media.MediaFormat
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.atFixedRate
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity

class ScreenRecorder(private val videoFormat: VideoFormat) {
    private lateinit var encoder: VideoEncoder
    private val screenBuffer = ScreenBuffer(videoFormat.width, videoFormat.height)
    private lateinit var job: Job
    private var onEncodedFrameListener: (EncodedFrame) -> Unit = {}
    private var onOutputFormatChangedListener: (MediaFormat) -> Unit = {}

    private val recordingJob: suspend CoroutineScope.() -> Unit = {
        while (isActive) {
            ForegroundActivityHolder.withForegroundActivity {
                drawFrame(it)
            }
        }
    }

    fun start() {
        encoder = VideoEncoder(videoFormat)
        encoder.setOnEncodedListener(::onEncodedFrame)
        encoder.setOnOutputFormatChanged(::onOutputFormatChanged)
        job = GlobalScope.atFixedRate(Dispatchers.IO, videoFormat.frameTime, recordingJob)
        encoder.start()
    }

    fun stop(scope: CoroutineScope = GlobalScope) = scope.launch {
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        job.cancelAndJoin()
        encoder.stop()
        encoder.release()
    }

    fun setOnEncodedFrameListener(listener: (EncodedFrame) -> Unit) {
        onEncodedFrameListener = listener
    }

    fun setOnOutputFormatChangedListener(listener: (MediaFormat) -> Unit) {
        onOutputFormatChangedListener = listener
    }

    private fun onOutputFormatChanged(format: MediaFormat) = onOutputFormatChangedListener(format)

    private fun onEncodedFrame(frame: EncodedFrame) {
        Stats.onFrameEncoded(frame)
        onEncodedFrameListener(frame)
    }

    private suspend fun drawFrame(activity: Activity) {
        val rootLayout = activity.window.decorView
        if (rootLayout.width == 0 && rootLayout.height == 0) {
            return
        }
        encoder.drawFrame {
            screenBuffer.drawToCanvas(rootLayout, it)
        }
    }
}
