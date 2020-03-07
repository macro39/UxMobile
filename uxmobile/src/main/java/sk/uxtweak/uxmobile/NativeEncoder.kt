package sk.uxtweak.uxmobile

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.view.Surface
import java.nio.ByteBuffer

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class NativeEncoder(
    width: Int,
    height: Int,
    private val frameRate: Int
) {
    private val screenWidth = if (width % 2 != 0) width + 1 else width
    private val screenHeight = if (height % 2 != 0) height + 1 else height
    private val mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, screenWidth, screenHeight)
    private val encoder = MediaCodec.createEncoderByType(MIME_TYPE)
    private val bufferInfo = MediaCodec.BufferInfo()
    private lateinit var renderingSurface: Surface
    private var running = false

    private var onBufferReady: (ByteBuffer) -> Unit = {}

    init {
        logi(TAG, "NativeEncoder init($width, $height, $frameRate)")
        configureMediaFormat()
    }

    fun start() {
        logi(TAG, "Starting encoder")
        encoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        renderingSurface = encoder.createInputSurface()
        encoder.start()
        running = true
    }
    fun stop() {
        logi(TAG, "Stopping encoder")
        drainEncoder(true)
        running = false
        encoder.stop()
        renderingSurface.release()
    }

    fun setBufferListener(listener: (ByteBuffer) -> Unit) {
        onBufferReady = listener
    }

    fun encode(bitmap: Bitmap) {
        logd(TAG, "encode")
        if (!running) {
            logw(TAG, "Called encode, but encoder is not running!")
            return
        }
        drainEncoder(false)
        renderingSurface.tryLockCanvas {
            logd(TAG, "drawBitmap")
            drawBitmap(bitmap, 0F, 0F, null)
        }
    }

    @Synchronized
    private fun drainEncoder(endOfStream: Boolean) {
        logd(TAG, "drainEncoder($endOfStream)")
        if (endOfStream) {
            encoder.signalEndOfInputStream()
        }

        while (running) {
            val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT)

            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                logd(TAG, "INFO_TRY_AGAIN_LATER($endOfStream)")
                break
            } else if (encoderStatus >= 0) {
                val encodedData = encoder.getOutputBuffer(encoderStatus)!!
                if (bufferInfo.size > 0) {
                    onBufferReady(encodedData)
                }
                encoder.releaseOutputBuffer(encoderStatus, false)

                logd(TAG, "Received output buffer with size ${bufferInfo.size}")

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    logd(TAG, "BUFFER_FLAG_END_OF_STREAM")
                    break
                }
            }
        }
        logd(TAG, "end drainEncoder($endOfStream)")
    }

    fun release() {
        logi(TAG, "Finishing and releasing encoder")

        drainEncoder(true)
        running = false
        encoder.flush()
        encoder.stop()
        encoder.release()

        renderingSurface.release()
    }

    private fun configureMediaFormat() {
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
    }

    private fun Surface.tryLockCanvas(body: Canvas.() -> Unit) {
        var canvas: Canvas? = null
        try {
            canvas = lockCanvas(null)
            body(canvas)
        } finally {
            unlockCanvasAndPost(canvas)
        }
    }

    companion object {
        const val TAG = "UxMobile"
        const val MIME_TYPE = "video/avc"
        const val BIT_RATE = 150_000
        const val I_FRAME_INTERVAL = 10
        const val TIMEOUT = 10_000L
    }
}
