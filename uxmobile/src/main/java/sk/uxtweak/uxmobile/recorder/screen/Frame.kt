package sk.uxtweak.uxmobile.recorder.screen

import android.media.MediaCodec
import java.nio.ByteBuffer

data class EncodedFrame(
    val buffer: ByteBuffer,
    val bufferInfo: MediaCodec.BufferInfo
)

inline class InputFrame(val data: ByteArray)

val EncodedFrame.isKeyFrame
    get() = bufferInfo.flags and MediaCodec.BUFFER_FLAG_KEY_FRAME != 0
