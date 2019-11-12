package sk.uxtweak.uxmobile

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenBuffer(
    private val width: Int,
    private val height: Int
) {
    private val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)
    private val renderingMatrix = Matrix()
    private val landscape = isLandscape(width, height)

    private var empty = true
    private var initialized = false

    @Synchronized
    suspend fun drawToBuffer(view: View) {
        val viewWidth = view.width
        val viewHeight = view.height

        if (!initialized) {
            val scaleX = width / viewWidth.toFloat()
            val scaleY = height / viewHeight.toFloat()

            renderingMatrix.setScale(scaleX, scaleY)

            canvas.setMatrix(renderingMatrix)

            initialized = true
        }

        if (landscape != isLandscape(viewWidth, viewHeight)) {
            canvas.save()

            canvas.translate(0f, viewWidth.toFloat())
            canvas.rotate(ROTATION_ORIENTATION)
            view.draw(canvas)

            canvas.restore()
        } else {
            withContext(Dispatchers.Main) { view.draw(canvas) }
        }

        empty = false
    }

    @Synchronized
    fun getBitmap() = bitmap!!

    @Synchronized
    fun isEmpty() = empty

    private fun isLandscape(width: Int, height: Int) = width > height

    companion object {
        const val ROTATION_ORIENTATION = -90.0f
    }
}
