package sk.uxtweak.uxmobile.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector : SensorEventListener {
    private var listener: OnShakeListener? = null
    private var shakeTimestamp: Long = 0
    private var shakeCount = 0

    fun setOnShakeListener(listener: OnShakeListener?) {
        this.listener = listener
    }

    interface OnShakeListener {
        fun onShake(count: Int)
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (listener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            val gForce: Float =
                sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0
                }
                shakeTimestamp = now
                shakeCount++
                listener!!.onShake(shakeCount)
            }
        }
    }

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }
}
