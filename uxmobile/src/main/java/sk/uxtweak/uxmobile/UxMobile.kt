package sk.uxtweak.uxmobile

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.MainThread
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.UxMobile.start
import sk.uxtweak.uxmobile.core.EventRecorder
import sk.uxtweak.uxmobile.core.VideoRecorder
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.rpc.RpcManager
import java.io.IOException

/**
 * Main class that initializes agent for tracking events. To start tracking events call [start].
 */
object UxMobile {
    private const val TAG = "UxMobile"
    private const val API_KEY = "UxMobileApiKey"

    /**
     * Application Context. Initialized by
     * [sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycleInitializer] before application starts.
     */
    private lateinit var application: Application

    private lateinit var sessionAgent: SessionAgent
    private lateinit var eventRecorder: EventRecorder
    private lateinit var videoRecorder: VideoRecorder
    private lateinit var eventsSocket: WebSocketClient
    private lateinit var eventsServer: EventServer
    private lateinit var rpcManager: RpcManager

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
            Log.e(TAG, "Cannot load API key! Check if you have your API key declared in Android Manifest")
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
        ForegroundActivityHolder.register()
        eventRecorder = EventRecorder()
        eventsSocket = WebSocketClient(BuildConfig.EVENTS_SERVER_URL)
        eventsServer = EventServer(eventsSocket)
        sessionAgent = SessionAgent(eventsServer, eventRecorder)
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
}
