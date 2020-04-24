package sk.uxtweak.uxmobile.recorder.screen

import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface
import kotlinx.coroutines.*
import sk.uxtweak.uxmobile.core.copy

class VideoEncoder(private val videoFormat: VideoFormat) {
    private val dispatcher = newSingleCoroutineDispatcher(DISPATCHER_NAME)
    private var job: Job? = null
    private val encoder = MediaCodec.createEncoderByType(VideoFormat.VIDEO_FORMAT)
    private val info = MediaCodec.BufferInfo()
    private lateinit var surface: Surface
    private var onEncodedListener: (EncodedFrame) -> Unit = {}
    private var onOutputFormatChanged: (MediaFormat) -> Unit = {}

    private val encoderJob: suspend CoroutineScope.() -> Unit = {
        while (isActive) {
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

    fun configure() {
        encoder.configureEncoder(videoFormat)
        surface = encoder.createInputSurface()
    }

    fun start() {
        if (job != null && job?.isActive == true) {
            throw IllegalStateException("Encoder already started, must be stopped first")
        }
        configure()
        encoder.start()
        job = GlobalScope.launch(dispatcher, block = encoderJob)
    }

    fun stop() = runBlocking {
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        if (job?.isActive == false) {
            throw IllegalStateException("Encoder must be started first")
        }
        job!!.cancelAndJoin()
        encoder.stop()
        surface.release()
    }

    fun release() {
        if (job?.isActive == true) {
            throw IllegalStateException("Must be first stopped before releasing")
        }
        encoder.release()
    }

    suspend fun drawFrame(block: suspend (Canvas) -> Unit) = surface.withLockedCanvas {
        block(this)
    }

    fun setOnEncodedListener(listener: (EncodedFrame) -> Unit) {
        onEncodedListener = listener
    }

    fun setOnOutputFormatChanged(listener: (MediaFormat) -> Unit) {
        onOutputFormatChanged = listener
    }

    companion object {
        private const val DEFAULT_DEQUEUE_TIMEOUT = 10L * 1000L
        private const val DISPATCHER_NAME = "Encoder"
    }
}
