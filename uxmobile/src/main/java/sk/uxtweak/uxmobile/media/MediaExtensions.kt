package sk.uxtweak.uxmobile.media

import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaCodec
import android.view.Surface
import kotlinx.coroutines.asCoroutineDispatcher
import sk.uxtweak.uxmobile.util.NamedThreadFactory
import java.nio.ByteBuffer
import java.util.concurrent.Executors

fun MediaCodec.configureEncoder(videoFormat: VideoFormat) {
    configure(videoFormat(), null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
}

fun Surface.withLockedCanvas(block: Canvas.() -> Unit) {
    var canvas: Canvas? = null
    try {
        canvas = lockCanvas(null)
        block(canvas)
    } finally {
        unlockCanvasAndPost(canvas)
    }
}

fun Canvas.drawBitmap(bitmap: Bitmap) {
    drawBitmap(bitmap, 0F, 0F, null)
}

fun newSingleCoroutineDispatcher(name: String) =
    Executors.newSingleThreadExecutor(NamedThreadFactory(name)).asCoroutineDispatcher()

operator fun ByteBuffer.plusAssign(buffer: ByteBuffer) {
    while (hasRemaining() && buffer.hasRemaining()) {
        put(buffer.get())
    }
}

fun ByteBuffer.flushBuffer(block: (ByteBuffer) -> Unit) {
    flip()
    block(this)
    clear()
}
