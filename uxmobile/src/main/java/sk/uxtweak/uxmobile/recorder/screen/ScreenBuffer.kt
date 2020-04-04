package sk.uxtweak.uxmobile.recorder.screen

import android.graphics.Canvas
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sk.uxtweak.uxmobile.util.ScreenUtils

class ScreenBuffer(
    private val width: Int,
    private val height: Int
) {
    private val landscape = ScreenUtils.isLandscape(width, height)

    suspend fun drawToCanvas(view: View, canvas: Canvas) {
        val viewWidth = view.width
        val viewHeight = view.height

        canvas.scale(width / viewWidth.toFloat(), height / viewHeight.toFloat())

        if (landscape != ScreenUtils.isLandscape(viewWidth, viewHeight)) {
            canvas.translate(0F, viewWidth.toFloat())
            canvas.rotate(ROTATION_ORIENTATION)
        }
        withContext(Dispatchers.Main) { view.draw(canvas) }
    }

    companion object {
        private const val ROTATION_ORIENTATION = -90.0F
    }
}
