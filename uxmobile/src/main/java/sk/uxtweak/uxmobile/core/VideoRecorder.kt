package sk.uxtweak.uxmobile.core

import android.app.Activity
import kotlinx.coroutines.Dispatchers
import sk.uxtweak.uxmobile.*
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity
import java.nio.ByteBuffer

class VideoRecorder(
    screenWidth: Int,
    screenHeight: Int,
    private val frameRate: Int = 25
) {
    private val encoder = NativeEncoder(screenWidth, screenHeight, frameRate)
    private val screenBuffer = ScreenBuffer(screenWidth, screenHeight)

    private val buffer = ByteBuffer.allocate(BUFFER_SIZE)

    private var bufferReadyListener: (ByteBuffer) -> Unit = {}

    init {
        encoder.setBufferListener(::onBufferAvailable)
    }

    fun start() {
        logd(TAG, "Starting video recording")
        encoder.start()
        startEncoding()
    }

    fun stop() {
        encoder.stop()
    }

    fun startEncoding() {
        ForegroundScope.atFixedRate(Dispatchers.IO, 1000L / frameRate) {
            ForegroundActivityHolder.withForegroundActivity {
                captureFrame(it)
                encodeFrame()
            }
        }
    }

    fun setBufferReadyListener(listener: (ByteBuffer) -> Unit) {
        bufferReadyListener = listener
    }

    private fun onBufferAvailable(data: ByteBuffer) {
        if (buffer.remaining() >= data.limit()) {
            buffer.put(data)
        } else {
            while (buffer.hasRemaining()) buffer.put(data.get())
            buffer.flip()
            bufferReadyListener(buffer)
            buffer.clear()
            buffer.put(data)
        }
    }

    private suspend fun captureFrame(activity: Activity) {
        val rootLayout = activity.window.decorView
        if (rootLayout.width == 0 && rootLayout.height == 0) {
            return
        }
        screenBuffer.drawToBuffer(rootLayout)
    }

    private fun encodeFrame() {
        if (!screenBuffer.isEmpty()) {
            encoder.encode(screenBuffer.getBitmap())
        } else {
            logw(TAG, "Screen buffer is empty while encoding!")
        }
    }

    companion object {
        const val TAG = "UxMobile"
        const val BUFFER_SIZE = 1024 * 1024
    }
}
