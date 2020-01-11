package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.Dispatchers
import sk.uxtweak.uxmobile.ForegroundScope
import sk.uxtweak.uxmobile.NativeEncoder
import sk.uxtweak.uxmobile.ScreenBuffer
import sk.uxtweak.uxmobile.adapter.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.atFixedRate
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle.currentActivity
import java.io.IOException
import java.nio.ByteBuffer

class VideoRecorder(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val bitRate: Int,
    private val frameRate: Int
) : LifecycleObserverAdapter() {
    private lateinit var encoder: NativeEncoder
    private lateinit var screenBuffer: ScreenBuffer

    private val buffer = ByteBuffer.allocate(BUFFER_SIZE)

    private var bufferReadyListener: (ByteBuffer) -> Unit = {}

    fun setBufferReadyListener(listener: (ByteBuffer) -> Unit) {
        bufferReadyListener = listener
    }

    override fun onFirstActivityStarted(activity: Activity) {
        Log.d(TAG, "onFirstActivityStarted: starting video recording")
        try {
            encoder = NativeEncoder(screenWidth, screenHeight, frameRate, bitRate)
            encoder.setBufferListener(::onBufferAvailable)
            screenBuffer = ScreenBuffer(screenWidth, screenHeight)

            ForegroundScope.atFixedRate(Dispatchers.Default, 1000L / frameRate) {
                captureFrame()
                encodeFrame()
            }
        } catch (e: IOException) {
            Log.e(TAG, "doInBackground: ", e)
        }
    }

    override fun onLastActivityStopped(activity: Activity) {
        super.onLastActivityStopped(activity)
        Log.d(TAG, "onLastActivityStopped: stopping video recording")

        try {
            encoder.finish()
        } catch (e: IOException) {
            Log.e(TAG, "Cannot finish encoder: ", e)
        }

        buffer.flip()
        bufferReadyListener(buffer)
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

    private suspend fun captureFrame() {
        val rootLayout = currentActivity?.get()?.window?.decorView
        if (rootLayout == null || (rootLayout.width == 0 && rootLayout.height == 0)) {
            return
        }
        screenBuffer.drawToBuffer(rootLayout)
    }

    private fun encodeFrame() {
        try {
            if (!screenBuffer.isEmpty()) {
                encoder.encodeFrame(screenBuffer.getBitmap())
            }
        } catch (e: IOException) {
            Log.e(TAG, "encodeFrame: ", e)
        }
    }

    companion object {
        const val TAG = "UxMobile"
        const val BUFFER_SIZE = 1024 * 1024
    }
}
