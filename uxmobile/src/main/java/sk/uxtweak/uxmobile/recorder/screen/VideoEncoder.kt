package sk.uxtweak.uxmobile.recorder.screen

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface
import sk.uxtweak.uxmobile.core.copy
import sk.uxtweak.uxmobile.core.logi
import sk.uxtweak.uxmobile.core.logw
import sk.uxtweak.uxmobile.util.NamedThreadFactory
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private typealias OnEncodedListener = (EncodedFrame) -> Unit
private typealias OnOutputFormatChanged = (MediaFormat) -> Unit

class VideoEncoder(videoFormat: VideoFormat) {
    private val executor =
        Executors.newSingleThreadExecutor(NamedThreadFactory("Encoder thread", Thread.MAX_PRIORITY))
    private val encoder = MediaCodec.createEncoderByType(VideoFormat.VIDEO_FORMAT)
    private val info = MediaCodec.BufferInfo()
    private lateinit var surface: Surface
    private lateinit var future: Future<*>
    private var onEncodedListener: OnEncodedListener = {}
    private var onOutputFormatChanged: OnOutputFormatChanged = {}

    @Volatile
    private var isRunning = false

    private val job = Runnable {
        while (isRunning) {
            val index = encoder.dequeueOutputBuffer(info, DEFAULT_DEQUEUE_TIMEOUT)
            if (index == MediaCodec.INFO_TRY_AGAIN_LATER || index < 0) {
                continue
            }
            if (info.flags and MediaCodec.INFO_OUTPUT_FORMAT_CHANGED != 0) {
                onOutputFormatChanged(encoder.outputFormat)
                continue
            }
            if (info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                continue
            }
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }
            val buffer = encoder.getOutputBuffer(index)
            val frame = EncodedFrame(buffer!!.copy(), info.duplicate())
            onEncodedListener(frame)
            encoder.releaseOutputBuffer(index, false)
        }
    }

    init {
        configure(videoFormat)
    }

    fun configure(videoFormat: VideoFormat) {
        encoder.configureEncoder(videoFormat)
        surface = encoder.createInputSurface()
    }

    fun start() {
        if (isRunning) {
            throw IllegalStateException("Encoder already started, must be stopped first")
        }
        encoder.start()
        isRunning = true
        future = executor.submit(job)
    }

    fun stop() {
        if (!isRunning) {
            throw IllegalStateException("Encoder must be started first")
        }
        isRunning = false
        joinJob()
        encoder.stop()
    }

    fun release() {
        if (isRunning) {
            throw IllegalStateException("Must be first stopped before releasing")
        }
        surface.release()
        encoder.release()
    }

    fun encode(bitmap: Bitmap) = surface.withLockedCanvas {
        drawBitmap(bitmap)
    }

    fun setOnEncodedListener(listener: OnEncodedListener) {
        onEncodedListener = listener
    }

    fun setOnOutputFormatChanged(listener: OnOutputFormatChanged) {
        onOutputFormatChanged = listener
    }

    private fun joinJob() {
        try {
            future.get(SHUTDOWN_TIMEOUT, SHUTDOWN_TIME_UNIT)
        } catch (exception: TimeoutException) {
            logw(
                TAG,
                "Timeout when waiting to finish encoder job, shutting down executor!"
            )
            shutdownExecutor()
        }
    }

    private fun shutdownExecutor() {
        executor.shutdown()
        if (!executor.awaitTermination(SHUTDOWN_TIMEOUT, SHUTDOWN_TIME_UNIT)) {
            logw(
                TAG,
                "Executor didn't shutdown gracefully in time limit, shutting down forcefully!"
            )
            val message = buildString {
                append("Tasks that didn't shutdown gracefully: ")
                executor.shutdownNow().also {
                    append("Task: $it")
                }
            }
            logi(TAG, message)
        }
    }

    companion object {
        private const val TAG = "UxMobile"
        private const val DEFAULT_DEQUEUE_TIMEOUT = 10L * 1000L
        private const val SHUTDOWN_TIMEOUT = 1L
        private val SHUTDOWN_TIME_UNIT = TimeUnit.SECONDS
    }
}
