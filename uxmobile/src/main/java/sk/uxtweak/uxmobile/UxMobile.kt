package sk.uxtweak.uxmobile

import android.app.Application
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.UxMobile.start
import sk.uxtweak.uxmobile.core.EventRecorder
import sk.uxtweak.uxmobile.core.VideoRecorder
import sk.uxtweak.uxmobile.lifecycle.SessionAgent
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.repository.EventsDatabase
import sk.uxtweak.uxmobile.repository.LocalEventStore
import java.nio.ByteBuffer

/**
 * Main class that initializes agent for tracking events. To start tracking events call [start].
 */
object UxMobile {
    private const val TAG = "UxMobile"
    private const val API_KEY = "ApiKey"

    /**
     * Application Context. Initialized by
     * [sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycleInitializer] before application starts.
     */
    private lateinit var application: Application

    private lateinit var agent: SessionAgent

    private lateinit var eventRecorder: EventRecorder
    private lateinit var videoRecorder: VideoRecorder
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var localEventStore: LocalEventStore

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
     *     <meta-data android:name="ApiKey" android:value="your-api-key" />
     */
    @JvmStatic
    fun start() {
        try {
            startInternal(loadApiKeyFromManifest())
        } catch (exception: IllegalStateException) {
            Log.e(TAG, "Cannot load API key! Check if you have API key in Android Manifest")
        }
    }

    /**
     * Should be called in [Application.onCreate] before any activity, service or receiver
     * is created. This method must be called from the main thread. If the API key is not valid,
     * the session will be recorded but not send to server.
     * @param apiKey UxMobile API key
     */
    @JvmStatic
    fun start(apiKey: String) = startInternal(apiKey)

    private fun startInternal(apiKey: String) {
        eventRecorder = EventRecorder()
//        videoRecorder = VideoRecorder(1440, 2960, NativeEncoder.VARIABLE_BIT_RATE, 60)
        videoRecorder = VideoRecorder(1440, 2960, 6 * 1000 * 1000, 2)
        videoRecorder.setBufferReadyListener {
            Log.d(TAG, "Buffer ready (${it.limit()})")
            val copy = ByteBuffer.allocate(it.limit())
            copy.put(it)
            ForegroundScope.launch {
                webSocketClient.emit("video", Base64.encodeToString(copy.array(), Base64.DEFAULT))
            }
        }
        webSocketClient = WebSocketClient("ws://vlado5678.ynet.sk:8000/asyngular/")

        ForegroundScope.launch {
            webSocketClient.connect()
        }

        val database = Room.databaseBuilder(
            application,
            EventsDatabase::class.java,
            "events-database"
        ).build()
        localEventStore = LocalEventStore(database)

        agent = SessionAgent(eventRecorder, webSocketClient, localEventStore)
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
     * Sets if sessions should be recorded even without session config when there is no access
     * to config server.
     * @param shouldRecord whether sessions should be recorded even when device is offline
     */
    @JvmStatic
    fun setRecordWhenOffline(shouldRecord: Boolean) {

    }
}
