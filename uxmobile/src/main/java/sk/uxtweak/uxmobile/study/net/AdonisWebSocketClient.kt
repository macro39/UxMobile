package sk.uxtweak.uxmobile.study.net

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import sk.uxtweak.uxmobile.lifecycle.ForegroundScope
import sk.uxtweak.uxmobile.study.Constants
import tech.gusavila92.websocketclient.WebSocketClient
import java.net.URI
import kotlin.coroutines.resumeWithException


/**
 * Created by Kamil Macek on 15.4.2020.
 */
class AdonisWebSocketClient(url: String) : WebSocketClient(URI(url)) {

    private val TAG = this::class.java.simpleName

    private var isConnected = false
    private var isJoined = false

    private var pingRemainingAttempts = 3
    private var pingAttempts = 3
    private var pingInterval = 5000L

    private var connectContinuation: CancellableContinuation<Any>? = null
    private var joinContinuation: CancellableContinuation<Any>? = null
    private var sendContinuation: CancellableContinuation<Any>? = null
    private var sendAnswersContinuation: CancellableContinuation<Any>? = null

    init {
        this.enableAutomaticReconnection(2000)
    }

    suspend fun waitForConnect() = suspendCancellableCoroutine<Any> {
        connectContinuation = it
        this.connect()
    }

    private suspend fun joinChannel() = suspendCancellableCoroutine<Any> {
        joinContinuation = it

        val data = JSONObject()
        data.put("t", 1)

        val topic = JSONObject()
        topic.put("topic", Constants.ADONIS_TOPIC)

        data.put("d", topic)

        this.send(data.toString())
    }

    suspend fun sendData(event: String, dataToSend: JSONObject) = suspendCancellableCoroutine<Any> {
        sendContinuation = it

        val data = JSONObject()
        try {
            val topic = JSONObject()
            topic.put("topic", Constants.ADONIS_TOPIC)
            topic.put("event", event)
            topic.put("data", dataToSend)

            data.put("t", 7)
            data.put("d", topic)

            Log.d(
                TAG,
                "Try to send data $data"
            )

            this.send(data.toString())
        } catch (e: JSONException) {
            Log.e(
                TAG,
                "Try to send data with wrong JSON format, data: $data"
            )
        }
    }

    suspend fun sendStudyAnswers(dataToSend: JSONObject) = suspendCancellableCoroutine<Any> {
        sendAnswersContinuation = it

        if (!isConnected) {
            resumeSendAnswersContinuationWithException(Exception("Socket not connected"))
            return@suspendCancellableCoroutine
        }

        val data = JSONObject()
        try {
            val topic = JSONObject()
            topic.put("topic", Constants.ADONIS_TOPIC)
            topic.put("event", Constants.ADONIS_EVENT_SEND_STUDY_ANSWERS)
            topic.put("data", dataToSend)

            data.put("t", 7)
            data.put("d", topic)

            Log.v(
                TAG,
                "Try to send data $data"
            )

            this.send(data.toString())
        } catch (e: JSONException) {
            Log.e(
                TAG,
                "Try to send data with wrong JSON format, data: $data"
            )
        }
    }

    fun closeConnection() {
        if (isConnected) {
            isConnected = false
            isJoined = false
            this.close()
            Log.d(TAG, "Socket closed")
        }
    }

    fun startPinging() {
        GlobalScope.launch {

            async {

                while (isConnected) {

                    if (pingRemainingAttempts > 0) {
                        try {
                            val text = JSONObject()
                            text.put("t", 8)

                            Log.v(
                                TAG,
                                "Ping sent $text"
                            )

                            send(text.toString())
                            pingRemainingAttempts--

                            delay(pingInterval)
                        } catch (e: JSONException) {
                            Log.e(
                                TAG,
                                "Try to send data with wrong JSON format, data: $e"
                            )
                        } catch (e: java.lang.Exception) {
                            Log.d("HAHA", e.message)
                        }
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onOpen() {
        Log.d(TAG, "Socket opened")
        isConnected = true

        if (!isJoined) {
            ForegroundScope.launch {
                joinChannel()
            }
        }

        startPinging()

        val cont = connectContinuation
        connectContinuation = null
        cont?.resume("") {}
    }

    @ExperimentalCoroutinesApi
    override fun onTextReceived(message: String?) {
        Log.d(TAG, "Received $message")

        val `object` = JSONObject(message)

        when (`object`.optInt("t")) {
            0 -> {
//                val data = `object`.optJSONObject("d")
////                pingInterval = data.optLong("clientInterval")
//                pingAttempts = data.optInt("clientAttempts")
//                startPinging()
            }
            3 -> {
                isJoined = true
                resumeJoinContinuation(message!!)
            }
            7 -> {
                val data = `object`.getJSONObject("d")
                when (data.optString("event")) {
                    Constants.ADONIS_EVENT_QUIT -> {
                        resumeContinuation(data)
                    }
                    Constants.ADONIS_EVENT_SEND_QUESTIONNAIRE -> {
                        try {
                            if (data.getJSONObject("data")
                                    .getString("error") == "no questionnaire"
                            ) {
                                Log.d(TAG, "No questionnaire found")
                            }
                        } catch (e: java.lang.Exception) {
                            Log.d(TAG, e.message)
                            resumeContinuation(data)
                        }
                    }
                    Constants.ADONIS_EVENT_SEND_STUDY -> {
                        try {
                            if (data.getJSONObject("data").getString("error") == "no studies") {
                                Log.d(TAG, "No studies found")
                            }
                        } catch (e: java.lang.Exception) {
                            Log.d(TAG, e.message)
                            resumeContinuation(data)
                        }
                    }
                    Constants.ADONIS_EVENT_SEND_STUDY_ANSWERS -> {
                        resumeSendAnswersContinuation(data)
                    }
                }
            }
            9 -> {
                pingRemainingAttempts = pingAttempts
            }
        }
    }

    override fun onBinaryReceived(data: ByteArray?) {
        Log.d(TAG, "Received binary " + data?.toString(Charsets.UTF_8))
    }

    @ExperimentalCoroutinesApi
    override fun onException(e: Exception?) {
        Log.d(TAG, "Exception: " + e?.message)
        isConnected = false
        isJoined = false

        resumeContinuationWithException(e!!)
        resumeJoinContinuationWithException(e)
        resumeSendAnswersContinuationWithException(e)
    }

    override fun onCloseReceived() {
        Log.d(TAG, "Socket closed")
        isConnected = false
    }

    override fun onPingReceived(data: ByteArray?) {
    }

    override fun onPongReceived(data: ByteArray?) {
    }

    @ExperimentalCoroutinesApi
    fun resumeContinuation(data: Any) {
        val cont = sendContinuation
        sendContinuation = null
        cont?.resume(data) {}
    }

    private fun resumeContinuationWithException(exception: java.lang.Exception) {
        val cont = sendContinuation
        sendContinuation = null
        cont?.resumeWithException(exception)
    }


    @ExperimentalCoroutinesApi
    fun resumeJoinContinuation(data: Any) {
        val cont = joinContinuation
        joinContinuation = null
        cont?.resume(data) {}
    }

    private fun resumeJoinContinuationWithException(exception: java.lang.Exception) {
        val cont = joinContinuation
        joinContinuation = null
        cont?.resumeWithException(exception)
    }

    @ExperimentalCoroutinesApi
    fun resumeSendAnswersContinuation(data: Any) {
        val cont = sendAnswersContinuation
        sendAnswersContinuation = null
        cont?.resume(data) {}
    }

    private fun resumeSendAnswersContinuationWithException(exception: java.lang.Exception) {
        val cont = sendAnswersContinuation
        sendAnswersContinuation = null
        cont?.resumeWithException(exception)
    }
}
