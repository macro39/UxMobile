package sk.uxtweak.uxmobile.media

import android.graphics.Bitmap
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.ForegroundScope
import sk.uxtweak.uxmobile.copy
import sk.uxtweak.uxmobile.logd
import java.nio.ByteBuffer

private typealias BufferCallback = (ByteBuffer) -> Unit

class ScreenEncoder(private val videoFormat: VideoFormat) {
    private val encoder = VideoEncoder()
    private val dispatcher = newSingleCoroutineDispatcher(DISPATCHER_NAME)
    private val buffer = ByteBuffer.allocate(BUFFER_SIZE)
    private var onBufferReady: BufferCallback = {}
    private lateinit var drainJob: Job

    fun start() {
        logd(TAG, "Starting encoder")
        encoder.configure(videoFormat)
        encoder.start()
        drainJob = ForegroundScope.drainEncoder()
    }

    suspend fun stop() {
        logd(TAG, "Stopping encoder")
        drainJob.cancelAndJoin()
        encoder.stop()
        flush()
    }

    fun release() = encoder.release()

    fun encode(bitmap: Bitmap) = encoder.encode(bitmap)

    fun setOnBufferReady(callback: BufferCallback) {
        onBufferReady = callback
    }

    private fun flush() = buffer.flushBuffer {
        onBufferReady(it.copy())
    }

    private fun CoroutineScope.drainEncoder() = launch(dispatcher) {
        while (isActive) {
            encoder.withOutputBuffer(this) { output ->
                buffer += output
                if (output.hasRemaining()) {
                    flush()
                    buffer += output
                }
            }
        }
    }

    companion object {
        private const val TAG = "UxMobile"
        private const val DISPATCHER_NAME = "Encoder Loop"
        private const val BUFFER_SIZE = 2 * 1024 * 1024
    }
}
