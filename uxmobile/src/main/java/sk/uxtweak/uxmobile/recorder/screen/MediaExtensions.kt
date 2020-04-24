package sk.uxtweak.uxmobile.recorder.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaCodec
import android.view.Surface
import kotlinx.coroutines.asCoroutineDispatcher
import sk.uxtweak.uxmobile.util.NamedThreadFactory
import java.nio.ByteBuffer
import java.util.concurrent.Executors

fun MediaCodec.BufferInfo.duplicate(): MediaCodec.BufferInfo {
    val copy = MediaCodec.BufferInfo()
    copy.set(offset, size, presentationTimeUs, flags)
    return copy
}

fun MediaCodec.configureEncoder(videoFormat: VideoFormat) {
    configure(videoFormat(), null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
}

suspend fun Surface.withLockedCanvas(block: suspend Canvas.() -> Unit) {
    var canvas: Canvas? = null
    try {
        canvas = lockCanvas(null)
        block(canvas)
    } finally {
        unlockCanvasAndPost(canvas)
    }
}

fun newSingleCoroutineDispatcher(name: String) =
    Executors.newSingleThreadExecutor(NamedThreadFactory(name)).asCoroutineDispatcher()

operator fun ByteBuffer.plusAssign(buffer: ByteBuffer) {
    while (hasRemaining() && buffer.hasRemaining()) {
        put(buffer.get())
    }
}
