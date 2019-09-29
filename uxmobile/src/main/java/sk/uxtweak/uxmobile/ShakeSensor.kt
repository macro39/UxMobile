package sk.uxtweak.uxmobile

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeSensor : SensorEventListener {
    interface OnShakeListener {
        fun onShake(count: Int)
    }

    private var listener: OnShakeListener? = null
    private var shakeTimeStamp = 0L
    private var shakeCount = 0

    fun setOnShakeListener(listener: OnShakeListener) {
        this.listener = listener
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (listener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // gForce will be close to 1 when there is no movement.
            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (shakeTimeStamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }

                // reset the shake count after 3 seconds of no shakes
                if (shakeTimeStamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0
                }

                shakeTimeStamp = now
                shakeCount++

                listener?.onShake(shakeCount)
            }
        }
    }

    companion object {
        const val SHAKE_THRESHOLD_GRAVITY = 2.7F
        const val SHAKE_SLOP_TIME_MS = 500
        const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }
}
