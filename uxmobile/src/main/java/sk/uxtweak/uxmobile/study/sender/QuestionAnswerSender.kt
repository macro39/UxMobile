package sk.uxtweak.uxmobile.study.sender

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import sk.uxtweak.uxmobile.UxMobile
import sk.uxtweak.uxmobile.core.withFixedDelay
import sk.uxtweak.uxmobile.study.net.AdonisWebSocketClient
import sk.uxtweak.uxmobile.study.persister.QuestionAnswerDatabase
import java.lang.Exception


/**
 * Created by Kamil Macek on 14.4.2020.
 */
class QuestionAnswerSender(
    private val adonisWebSocketClient: AdonisWebSocketClient,
    private val database: QuestionAnswerDatabase
) {
    private val TAG = this::class.java.simpleName

    var isRunning: Boolean = false
        private set

    private lateinit var job: Job

    var lastDataToSend = false

    private val senderJob: suspend CoroutineScope.() -> Unit = {
        sendQuestionAnswers()
    }

    fun start() {
        isRunning = true
        Log.d(TAG, "Sender start")
        job = GlobalScope.withFixedDelay(Dispatchers.IO, 10000, senderJob)
    }

    fun stop() = runBlocking {
        Log.d(TAG, "Sender stop")
        UxMobile.adonisWebSocketClient.closeConnection()
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        job.cancelAndJoin()
        isRunning = false
    }

    private suspend fun sendQuestionAnswers() {
        val questionAnswers = database.questionAnswerDao().getAll()

        if (questionAnswers.isEmpty()) {
            Log.d(TAG, "No data to send")

            if (lastDataToSend) {
                stop()
            }

            return
        }

        for (questionAnswer in questionAnswers) {
            val jsonAnswerQuestion = JSONObject(questionAnswer.questionAnswer)

            Log.d(TAG, jsonAnswerQuestion.toString())

            try {
                val response = adonisWebSocketClient.sendStudyAnswers(jsonAnswerQuestion) as JSONObject

                if (response.optJSONObject("data").optString("data").isNotEmpty()) {
                    // success
                    Log.d(TAG, response.optJSONObject("data").optString("data"))
                    database.questionAnswerDao().deleteItem(questionAnswer)
                } else {
                    // error send
                    Log.e(TAG, response.optJSONObject("data").optString("error"))
                    continue
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cant't send answers from db")
            }
        }

        if (lastDataToSend) {
            stop()
        }
    }
}
