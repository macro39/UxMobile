package sk.uxtweak.uxmobile.recorder.screen

import android.media.MediaCodecInfo
import android.media.MediaFormat

class VideoFormat(
    val width: Int,
    val height: Int,
    frameRate: Int = DEFAULT_FRAME_RATE,
    bitRate: Int = DEFAULT_BIT_RATE,
    iFrameInterval: Int = DEFAULT_I_FRAME_INTERVAL
) {
    val frameTime = 1000L / frameRate

    private val mediaFormat = MediaFormat.createVideoFormat(VIDEO_FORMAT, width, height)

    init {
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)
    }

    operator fun invoke(): MediaFormat = mediaFormat

    companion object {
        const val VIDEO_FORMAT = "video/avc"
        private const val DEFAULT_FRAME_RATE = 25
        private const val DEFAULT_BIT_RATE = 500_000
        private const val DEFAULT_I_FRAME_INTERVAL = 10
    }
}
