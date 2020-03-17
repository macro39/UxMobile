package sk.uxtweak.uxmobile.media

import android.graphics.Bitmap
import android.media.MediaCodec
import android.os.SystemClock
import android.view.Surface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import sk.uxtweak.uxmobile.logd
import java.nio.ByteBuffer

class VideoEncoder {
    private val encoder = MediaCodec.createEncoderByType(VideoFormat.VIDEO_FORMAT)
    private val info = MediaCodec.BufferInfo()
    private lateinit var surface: Surface

    fun configure(videoFormat: VideoFormat) {
        encoder.configureEncoder(videoFormat)
        surface = encoder.createInputSurface()
    }

    fun start() = encoder.start()

    fun stop() = encoder.stop()

    fun release() {
        surface.release()
        encoder.release()
    }

    fun encode(bitmap: Bitmap) = surface.withLockedCanvas {
        drawBitmap(bitmap)
    }

    fun withOutputBuffer(scope: CoroutineScope, block: (ByteBuffer, Long) -> Unit) {
        while (scope.isActive) {
            val index = encoder.dequeueOutputBuffer(info, DEFAULT_DEQUEUE_TIMEOUT)
            if (index == MediaCodec.INFO_TRY_AGAIN_LATER || index < 0) {
                continue
            }
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            } else if (info.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME != 0) {
                logd("UxMobile", "Received key frame")
            }
            val buffer = encoder.getOutputBuffer(index)
            block(buffer!!, info.presentationTimeUs)
            encoder.releaseOutputBuffer(index, false)
        }
    }

    companion object {
        private const val DEFAULT_DEQUEUE_TIMEOUT = 10L * 1000L
    }
}