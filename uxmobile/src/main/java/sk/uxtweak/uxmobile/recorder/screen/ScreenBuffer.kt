package sk.uxtweak.uxmobile.recorder.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.graphics.applyCanvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sk.uxtweak.uxmobile.util.ScreenUtils

class ScreenBuffer(
    private val width: Int,
    private val height: Int
) {
    lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private val landscape = ScreenUtils.isLandscape(width, height)

    suspend fun drawToBitmap(view: View) {
        val viewWidth = view.width
        val viewHeight = view.height

        if (!this::bitmap.isInitialized) {
            bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap)
        }
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
