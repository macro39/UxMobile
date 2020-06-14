package sk.uxtweak.uxmobile.recorder.screen

import android.app.Activity
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.media.MediaFormat
import androidx.core.graphics.withTranslation
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.atFixedRate
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

class ScreenRecorder(val videoFormat: VideoFormat) {
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
    private var onFirstFrameListener: (Long) -> Unit = {}

    private val paint = Paint(Color.BLACK)
    private val filter = LightingColorFilter(0xFF7F7F7F.toInt(), 0x00000000)

    private val recordingJob: suspend CoroutineScope.() -> Unit = {
        ForegroundActivityHolder.withForegroundActivity {
            drawFrame(it)
        }
    }

    private val Any.isDecorView: Boolean
        get() = this::class.java.simpleName == DECOR_VIEW_CLASS_NAME

    init {
        paint.colorFilter = filter
    }

    fun start() {
        logd(TAG, "Starting screen recorder")
        Stats.onStartRecording()
        isRunning = true
        encoder = VideoEncoder(videoFormat)
        encoder.setOnEncodedListener(::onEncodedFrame)
        encoder.setOnOutputFormatChanged(::onOutputFormatChanged)
        encoder.setOnFirstFrameTime(::onFirstFrameTime)
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

    fun setOnFirstFrameDrawListener(listener: (Long) -> Unit) {
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

    private fun onFirstFrameTime(time: Long) {
        onFirstFrameListener(time)
    }

    private suspend fun drawFrame(activity: Activity) {
        val rootLayout = activity.window.decorView
        if (rootLayout.width == 0 && rootLayout.height == 0) {
            return
        }
        encoder.drawFrame {
            withContext(Dispatchers.Main) {
                it.scale(videoFormat.width / rootLayout.width.toFloat(), videoFormat.height / rootLayout.height.toFloat())
                val popupViews = activity.popupViews

                var drawnDimmed = false
                popupViews.filter { it.view.isDecorView }.forEachIndexed { index, view ->
                    if (index == 0) {
                        screenBuffer.drawToBitmap(rootLayout)
                    } else {
                        if (!drawnDimmed) {
                            drawnDimmed = true
                            it.drawBitmap(screenBuffer.bitmap, 0f, 0f, paint)
                        }
                        it.withTranslation(view.position.left.toFloat(), view.position.top.toFloat()) {
                            view.view.draw(this)
                        }
                    }
                }
                if (!drawnDimmed) {
                    it.drawBitmap(screenBuffer.bitmap, 0f, 0f, null)
                }

                popupViews.filter { !it.view.isDecorView }.forEach { view ->
                    it.withTranslation(view.position.left.toFloat(), view.position.top.toFloat()) {
                        view.view.draw(this)
                    }
                }
            }
        }
    }

    companion object {
        private const val DECOR_VIEW_CLASS_NAME = "DecorView"
    }
}
