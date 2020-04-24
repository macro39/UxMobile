package sk.uxtweak.uxmobile

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Environment
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import sk.uxtweak.uxmobile.UxMobile.start
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.lifecycle.ForegroundScope
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.StudyFlowController
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.net.AdonisWebSocketClient
import sk.uxtweak.uxmobile.study.net.JsonBuilder
import sk.uxtweak.uxmobile.study.utility.*
import sk.uxtweak.uxmobile.ui.DebugActivity
import sk.uxtweak.uxmobile.ui.ShakeDetector
import sk.uxtweak.uxmobile.util.IOUtils
import sk.uxtweak.uxmobile.util.logi
import java.io.File
import java.io.FileNotFoundException

/**
 * Main class that initializes agent for tracking events. To start the module, call [start].
 */
object UxMobile {
    private var started = false

    private lateinit var studyFlowController: StudyFlowController
    lateinit var sessionManager: SessionManager
    var apiKey: String? = null

    /**
     * Adonis web socket client - communicator with server
     */
    lateinit var adonisWebSocketClient: AdonisWebSocketClient

    /**
     * Application Context. Initialized by
     * [sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycleInitializer] before application starts.
     */
    private lateinit var application: Application

    /**
     * Called by [sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycleInitializer] to attach
     * application context to this class before [start] is called.
     * @param app application object to assign
     */
    internal fun initialize(app: Application) {
        application = app
    }

    /**
     * Should be called in [Application.onCreate] before any activity, service or receiver
     * is created. This method must be called from the main thread. If the API key is not found
     * or is invalid, [start] will print error log and do nothing.
     *
     * You can add your API key inside your *application* tag in your Android manifest like this
     *
     *     <meta-data android:name="UxMobileApiKey" android:value="your-api-key" />
     */
    @JvmStatic
    @MainThread
    fun start() {
        try {
            startInternal(loadApiKeyFromManifest())
        } catch (exception: IllegalStateException) {
            Log.e(
                TAG,
                "Cannot load API key! Check if you have your API key declared in Android Manifest"
            )
        }
    }

    /**
     * Should be called in [Application.onCreate] before any activity, service or receiver
     * is created. This method must be called from the main thread. If the API key is not valid,
     * the session will be recorded but not sent to the server.
     * @param apiKey UxMobile API key
     */
    @JvmStatic
    @MainThread
    fun start(apiKey: String) = startInternal(apiKey)

    private fun startInternal(apiKey: String) {
        Stats.init(application)
        logi(TAG, "Starting UxMobile")
        if (started) {
            throw IllegalStateException("UxMobile has already started!")
        }
        started = true
        this.apiKey = apiKey

        IOUtils.initialize(application)

        ForegroundActivityHolder.register(ApplicationLifecycle)

        adonisWebSocketClient = AdonisWebSocketClient(BuildConfig.MAIN_URL)

        sessionManager = SessionManager(application)
        studyFlowController = StudyFlowController(application.applicationContext, sessionManager)

        ForegroundScope.launch {
            adonisWebSocketClient.waitForConnect()

            this@UxMobile.apiKey = "f515d0cd7b88fbe502919395fa4c6c8d599e939d"   // my study

            val location = getLocation()

            // uncomment for Usability testing study
            val initializeJson = JsonBuilder(
                "sessionId" to sessionManager.persister.sessionId,
                "token" to "f515d0cd7b88fbe502919395fa4c6c8d599e939d",
                "location" to location,
                "brandOfDevice" to getDeviceBrand(),
                "ip" to getIpAddress(
                    application
                ),
                "operatingSystem" to getOperatingSystem()
            ).toJsonObject()

//            this@UxMobile.apiKey = "plugin_initialization_demo_token_new"       // test study

            // uncomment for test study
//            val initializeJson = JsonBuilder(
//                "sessionId" to sessionManager.persister.sessionId,
//                "token" to this.apiKey,
//                "location" to "demo location",
//                "brandOfDevice" to "demo brand",
//                "ip" to "demo ip",
//                "operatingSystem" to "demo operating system"
//            ).toJsonObject()

            try {
                val response =
                    adonisWebSocketClient.sendData(
                        Constants.ADONIS_EVENT_INITIALIZE,
                        initializeJson
                    )

                try {
                    val jsonResponse = JSONObject(response.toString())

                    when (jsonResponse.optString("event")) {
                        Constants.ADONIS_EVENT_SEND_QUESTIONNAIRE -> {
                            val studyQuestionnaire = Gson().fromJson(
                                jsonResponse.optJSONObject("data").optJSONObject("data").toString(),
                                StudyQuestionnaire::class.java
                            )

                            StudyDataHolder.screeningQuestionnaire = studyQuestionnaire

                            startStudyFlow()
                        }
                        Constants.ADONIS_EVENT_SEND_STUDY -> {
                            val study = Gson().fromJson(
                                jsonResponse.optJSONObject("data").optJSONObject("data").toString(),
                                Study::class.java
                            )

                            StudyDataHolder.setNewStudy(study)

                            startStudyFlow()
                        }
                        Constants.ADONIS_EVENT_QUIT -> {
                            Log.e(TAG, jsonResponse.optJSONObject("data").optString("error"))
                            return@launch
                        }
                        else -> {
                            Log.e(TAG, "Unexpected error when sending initialize: $jsonResponse")
                            return@launch
                        }
                    }
                } catch (e: JSONException) {
                    Log.e(
                        TAG,
                        "Cannot parse response: " + e.message
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Can't send initialize, no internet connection!")
            }
        }

        val sensorManager = ContextCompat.getSystemService(application, SensorManager::class.java)
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val shakeDetector = ShakeDetector()
        shakeDetector.setOnShakeListener(object : ShakeDetector.OnShakeListener {
            override fun onShake(count: Int) {
                if (count == 3) {
                    val intent = Intent(application, DebugActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ContextCompat.startActivity(application, intent, null)
                }
            }
        })

        sensorManager?.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    /**
     * Starts study flow activity
     */
    private fun startStudyFlow() {
        GlobalScope.launch(Dispatchers.Main) {
            studyFlowController.start()
        }
    }

    /**
     * Loads API key from meta-data in Android Manifest.
     * @return API key
     * @throws IllegalStateException if meta-data are not found inside Android Manifest
     */
    private fun loadApiKeyFromManifest(): String {
        val applicationInfo = application.packageManager.getApplicationInfo(
            application.packageName,
            PackageManager.GET_META_DATA
        )
        val apiKey = applicationInfo.metaData[API_KEY]
            ?: throw IllegalStateException("API key not specified!")
        return apiKey.toString()
    }

    /**
     * Loads API key from internal storage path. The API key file should be stored in specific
     * file on internal storage on the device.
     * @return API key
     * @throws IllegalStateException if app does not have the permission to read from internal storage
     * @throws FileNotFoundException if internal storage does not contain file with API key in it
     */
    @Deprecated("Should not be used on Android 10 or higher")
    private fun loadApiKeyFromStorage(): String {
        if (ContextCompat.checkSelfPermission(
                application,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw IllegalStateException("Read storage permission is not granted!")
        }
        val apiKeyFile = File(Environment.getExternalStorageDirectory(), API_KEY_FILE)
        if (!apiKeyFile.exists()) {
            throw FileNotFoundException("File with API key not found!")
        }
        return apiKeyFile.readText()
    }

    private const val TAG = "UxMobile"
    private const val API_KEY = "UxMobileApiKey"
    private const val API_KEY_FILE = "UxMobile/api.key"
}
