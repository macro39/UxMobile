package sk.uxtweak.uxmobile.recorder.screen

import android.app.Activity
import android.media.MediaFormat
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.atFixedRate
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

class ScreenRecorder(private val videoFormat: VideoFormat) {
    var isRunning: Boolean = false
        private set

    private lateinit var encoder: VideoEncoder
    private val screenBuffer = ScreenBuffer(videoFormat.width, videoFormat.height)
    private var format: MediaFormat? = null
    private var wasKeyFrame = false
    private lateinit var job: Job
    private var firstFrame = true
    private var onEncodedFrameListener: (EncodedFrame) -> Unit = {}
    private var onOutputFormatChangedListener: (MediaFormat) -> Unit = {}
    private var onFirstFrameListener: () -> Unit = {}

    private val recordingJob: suspend CoroutineScope.() -> Unit = {
        while (isActive) {
            ForegroundActivityHolder.withForegroundActivity {
                drawFrame(it)
            }
        }
    }

    fun start() {
        logd(TAG, "Starting screen recorder")
        Stats.onStartRecording()
        isRunning = true
        encoder = VideoEncoder(videoFormat)
        encoder.setOnEncodedListener(::onEncodedFrame)
        encoder.setOnOutputFormatChanged(::onOutputFormatChanged)
        encoder.start()
        job = GlobalScope.atFixedRate(Dispatchers.IO, videoFormat.frameTime, recordingJob)
    }

    fun stop(scope: CoroutineScope = GlobalScope) = scope.launch {
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        job.cancelAndJoin()
        encoder.stopAndJoin()
        encoder.release()
        wasKeyFrame = false
        format = null
        isRunning = false
        firstFrame = true
        Stats.onStopRecording()
    }

    fun setOnEncodedFrameListener(listener: (EncodedFrame) -> Unit) {
        onEncodedFrameListener = listener
        wasKeyFrame = false
    }

    fun setOnOutputFormatChangedListener(listener: (MediaFormat) -> Unit) {
        onOutputFormatChangedListener = listener
        if (format != null) {
            onOutputFormatChangedListener(format!!)
        }
    }

    fun setOnFirstFrameDrawListener(listener: () -> Unit) {
        onFirstFrameListener = listener
    }

    private fun onOutputFormatChanged(format: MediaFormat) {
        this.format = format
        onOutputFormatChangedListener(format)
    }

    private fun onEncodedFrame(frame: EncodedFrame) {
        if (frame.isKeyFrame) {
            wasKeyFrame = true
        }
        if (wasKeyFrame) {
            Stats.onFrameEncoded(frame)
            onEncodedFrameListener(frame)
        }
    }

    private suspend fun drawFrame(activity: Activity) {
        val rootLayout = activity.window.decorView
        if (rootLayout.width == 0 && rootLayout.height == 0) {
            return
        }
        if (firstFrame) {
            firstFrame = false
            onFirstFrameListener()
        }
        encoder.drawFrame {
            screenBuffer.drawToCanvas(rootLayout, it)
        }
    }
}
