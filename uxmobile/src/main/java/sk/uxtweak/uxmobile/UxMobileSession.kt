package sk.uxtweak.uxmobile

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.children
import sk.uxtweak.uxmobile.adapter.LifecycleObserver
import sk.uxtweak.uxmobile.core.EventRecorder
import sk.uxtweak.uxmobile.core.LifecycleCallback
import sk.uxtweak.uxmobile.util.Config

class UxMobileSession(
    application: Application,
    apiKey: String
) : LifecycleCallback {
    private val context: Context = application
    private val lifecycleObserver = LifecycleObserver(this)
    private val eventRecorder = EventRecorder()
    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeSensor

    // TODO: Fix VideoRecorder (optimize mostly)

    init {
        Log.d("UxMobile", "UxMobileSession: New UxMobile Session")

        Config.get().apiKey = apiKey

        registerCallbacks(application)
//        registerShakeSensor()

        MyExceptionHandler.register()
    }

    override fun onFirstActivityStarted(activity: Activity) {
        Log.d("UxMobile", "onFirstActivityStarted " + activity.componentName.shortClassName)

        registerShakeSensor()

        eventRecorder.onFirstActivityStarted(activity)
        onSessionStarted()
    }

    private fun onSessionStarted() {
        eventRecorder.onSessionStarted()
        sensorManager.registerListener(
            shakeDetector,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onEveryActivityStarted(activity: Activity) {
        eventRecorder.onEveryActivityStarted(activity)
    }

    override fun onEveryActivityStopped(activity: Activity) {
        eventRecorder.onEveryActivityStopped(activity)
    }

    override fun onLastActivityStopped(activity: Activity) {
        eventRecorder.onLastActivityStopped(activity)
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        eventRecorder.onConfigurationChanged(configuration)
    }

    private fun registerCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(lifecycleObserver)
        application.registerComponentCallbacks(lifecycleObserver)
    }

    private fun registerShakeSensor() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeSensor()
        shakeDetector.setOnShakeListener(object : ShakeSensor.OnShakeListener {
            override fun onShake(count: Int) {
                Log.d("UxMobile", "onShake: $count")
            }
        })
    }

    private fun unregisterShakeSensor() {
        sensorManager.unregisterListener(shakeDetector)
    }

    fun addCustomEvent(eventName: String) {
        eventRecorder.addCustomEvent(eventName)
    }

    fun addCustomEvent(eventName: String, payload: Map<String, String>) {
        eventRecorder.addCustomEvent(eventName, payload)
    }

    fun addExceptionEvent(throwable: Throwable) {
        eventRecorder.addExceptionEvent(throwable)
    }
}
