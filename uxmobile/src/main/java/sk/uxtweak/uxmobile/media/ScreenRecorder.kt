package sk.uxtweak.uxmobile.media

import android.app.Activity
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.*
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity
import java.nio.ByteBuffer

class ScreenRecorder(private val videoFormat: VideoFormat) {
    private val encoder = ScreenEncoder(videoFormat)
    private val screenBuffer = ScreenBuffer(videoFormat.width, videoFormat.height)
    private lateinit var encodeJob: Job

    val averageFrameRate: Float
        get() {
            val frameRate = 1000F / ((encoder.lastTime - encoder.firstTime) / 1000F / encoder.frames.toFloat())
            logd("UxMobile", "Frame Rate: $frameRate")
            return frameRate
        }

    fun start() {
        encoder.start()
        encodeJob = ForegroundScope.startEncoding(videoFormat.frameTime)
    }

    suspend fun stop() {
        encodeJob.cancelAndJoin()
        encoder.stop()
    }

    fun setOnBufferReady(callback: (ByteBuffer) -> Unit) = encoder.setOnBufferReady(callback)

    private fun CoroutineScope.startEncoding(rate: Long) = atFixedRate(Dispatchers.Encoder, rate) {
        ForegroundActivityHolder.withForegroundActivity {
            drawFrame(it)
            encodeFrame()
        }
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
            logw(TAG, "Screen buffer is empty while encoding!")
        }
    }

    companion object {
        private const val TAG = "UxMobile"
    }
}
