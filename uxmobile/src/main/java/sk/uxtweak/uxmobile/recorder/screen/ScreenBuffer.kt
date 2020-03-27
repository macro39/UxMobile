package sk.uxtweak.uxmobile.recorder.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.View
import androidx.core.graphics.withSave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sk.uxtweak.uxmobile.util.ScreenUtils

class ScreenBuffer(
    private val width: Int,
    private val height: Int
) {
    val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    var isEmpty = true
        private set

    private val canvas = Canvas(bitmap)
    private val matrix = Matrix()
    private val landscape = ScreenUtils.isLandscape(width, height)
    private var isInitialized = false

    private fun initialize(viewWidth: Float, viewHeight: Float) {
        val scaleX = width / viewWidth
        val scaleY = height / viewHeight

        matrix.setScale(scaleX, scaleY)
        canvas.setMatrix(matrix)
    }

    suspend fun drawToBuffer(view: View) {
        val viewWidth = view.width
        val viewHeight = view.height

        if (!isInitialized) {
            initialize(viewWidth.toFloat(), viewHeight.toFloat())
            isInitialized = true
        }

        if (landscape != ScreenUtils.isLandscape(viewWidth, viewHeight)) {
            canvas.withSave {
                canvas.translate(0F, viewWidth.toFloat())
                canvas.rotate(ROTATION_ORIENTATION)
                withContext(Dispatchers.Main) { view.draw(canvas) }
            }
        } else {
            withContext(Dispatchers.Main) { view.draw(canvas) }
        }
        isEmpty = false
    }

    companion object {
        const val ROTATION_ORIENTATION = -90.0F
    }
}
