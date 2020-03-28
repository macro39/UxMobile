package sk.uxtweak.uxmobile

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import sk.uxtweak.uxmobile.UxMobile.start
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.logi
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.sender.SessionManager
import sk.uxtweak.uxmobile.study.StudyFlowController
import java.io.File
import java.io.FileNotFoundException

/**
 * Main class that initializes agent for tracking events. To start the module, call [start].
 */
object UxMobile {
    private var started = false

    private lateinit var studyFlowController: StudyFlowController
    lateinit var sessionManager: SessionManager

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

        ForegroundActivityHolder.register(ApplicationLifecycle)

        sessionManager = SessionManager(application)
        studyFlowController = StudyFlowController(application.applicationContext, sessionManager)
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
