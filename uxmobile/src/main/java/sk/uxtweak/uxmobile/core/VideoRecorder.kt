package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.*
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.withForegroundActivity
import java.io.IOException
import java.nio.ByteBuffer

class VideoRecorder(
    screenWidth: Int,
    screenHeight: Int,
    bitRate: Int = 400000,  // 400Kbps
    private val frameRate: Int = 60
) : LifecycleObserverAdapter() {
    private val encoder = NativeEncoder(screenWidth, screenHeight, frameRate, bitRate)
    private val screenBuffer = ScreenBuffer(screenWidth, screenHeight)

    val buffer = ByteBuffer.allocate(BUFFER_SIZE)

    private var bufferReadyListener: (ByteBuffer) -> Unit = {}

    init {
        encoder.setBufferListener(::onBufferAvailable)
    }

    fun record() {
        logd(TAG, "Starting video recording")
        try {
            encoder.start()

            ForegroundScope.atFixedRate(Dispatchers.IO, 1000L / frameRate, onCancel = {
                encoder.stop()  // TODO: Maybe finish instead of stopping to release resources immediately
            }) {
                captureFrame()
                encodeFrame()
            }
        } catch (exception: IOException) {
            Log.e(TAG, "startRecording error: ", exception)
        }
    }

    fun finish() {
        try {
            encoder.finish()
        } catch (exception: IOException) {
            Log.e(TAG, "Cannot finish encoder: ", exception)
        }
    }

    fun setBufferReadyListener(listener: (ByteBuffer) -> Unit) {
        bufferReadyListener = listener
    }

    override fun onFirstActivityStarted(activity: Activity) {
        logd(TAG, "onFirstActivityStarted")
        record()
    }

    override fun onLastActivityStopped(activity: Activity) {
        super.onLastActivityStopped(activity)
        logd(TAG, "onLastActivityStopped")
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

    private suspend fun captureFrame() = ForegroundActivityHolder.withForegroundActivity {
        val rootLayout = it.window.decorView
        if (rootLayout.width == 0 && rootLayout.height == 0) {
            return
        }
        screenBuffer.drawToBuffer(rootLayout)
    }

    private fun encodeFrame() {
        try {
            if (!screenBuffer.isEmpty()) {
                encoder.encodeFrame(screenBuffer.getBitmap())
            }
        } catch (exception: IOException) {
            Log.e(TAG, "encodeFrame error: ", exception)
        }
    }

    companion object {
        const val TAG = "UxMobile"
        const val BUFFER_SIZE = 1024 * 1024
    }
}
