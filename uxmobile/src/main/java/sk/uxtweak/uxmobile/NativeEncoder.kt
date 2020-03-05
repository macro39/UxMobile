package sk.uxtweak.uxmobile

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Surface
import java.io.File
import java.nio.ByteBuffer

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class NativeEncoder(
    width: Int,
    height: Int,
    private val frameRate: Int,
    private val bitRate: Int
) {
    private val screenWidth = if (width % 2 != 0) width + 1 else width
    private val screenHeight = if (height % 2 != 0) height + 1 else height
    private val mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, screenWidth, screenHeight)
    private val encoder = MediaCodec.createEncoderByType(MIME_TYPE)
    private val muxer = MediaMuxer(File(Environment.getExternalStorageDirectory(), "video.mp4").absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    private val rect = Rect(0, 0, screenWidth, screenHeight)
    private val bufferInfo = MediaCodec.BufferInfo()
    private var renderingSurface: Surface? = null
    private var videoTrackIndex = 0

    var running = false

    private var bufferListener: (ByteBuffer) -> Unit = {}

    init {
        logi(TAG, "NativeEncoder init()")
        initMediaFormat()
    }

    fun start() {
        logi(TAG, "Starting encoder")
        encoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        videoTrackIndex = muxer.addTrack(encoder.outputFormat)
        muxer.start()

        renderingSurface = encoder.createInputSurface()
        encoder.start()
        running = true
    }

    fun stop() {
        logi(TAG, "Stopping encoder")
        drainEncoder(true)
        encoder.stop()
        running = false
    }

    fun setBufferListener(listener: (ByteBuffer) -> Unit) {
        bufferListener = listener
    }

    private fun initMediaFormat() {
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        // TODO: Check if crashes or not
        if (bitRate == VARIABLE_BIT_RATE) {
            mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR)
        }
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
    }

    private fun Surface.tryLockCanvas(dirtyRect: Rect, body: Canvas.() -> Unit) {
        var canvas: Canvas? = null
        try {
            if (!running) return
            canvas = lockCanvas(dirtyRect)
            body(canvas)
        } catch (e: Exception) {
            loge("UxMobile", "tryLockCanvas: ", e)
        } finally {
            canvas?.let { unlockCanvasAndPost(it) }
        }
    }

    fun encodeFrame(bitmap: Bitmap) {
        drainEncoder(false)
        // TODO: The not-null assertion operator (!!) is a code smell, do not use it in production code
        if (renderingSurface!!.isValid) {
            renderingSurface!!.tryLockCanvas(rect) {
                drawColor(0xff_00_00_00.toInt())
                drawBitmap(bitmap, 0f, 0f, null)
            }
        }
    }

    private fun drainEncoder(endOfStream: Boolean) {
        if (endOfStream) {
            encoder.signalEndOfInputStream()
        }

        while (running) {
            val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC)

            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                logi(TAG, "Encoder output format changed")
            } else if (encoderStatus < 0) {
                loge(TAG, "Encoder status < 0: $encoderStatus")
            } else {
                val encodedData = encoder.getOutputBuffer(encoderStatus)
                    ?: throw RuntimeException("encodedData $encoderStatus is null")

                if (bufferInfo.size != 0) {
                    muxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo)
                    bufferListener(encodedData)
                }

                encoder.releaseOutputBuffer(encoderStatus, false)

                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            }
        }
    }

    fun finish() {
        logi(TAG, "Finishing and releasing encoder")
        if (!running) {
            return
        }

        running = false
        drainEncoder(true)

        muxer.stop()
        muxer.release()

        encoder.stop()
        encoder.release()

        renderingSurface!!.release()
    }

    companion object {
        const val TAG = "UxMobile"
        const val MIME_TYPE = "video/avc"
        const val VARIABLE_BIT_RATE = 0
        const val IFRAME_INTERVAL = 5
        const val TIMEOUT_USEC = 10000L
    }
}
