package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.UxMobile
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.StudyFlowController
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.net.JsonBuilder
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder
import sk.uxtweak.uxmobile.util.TAG

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class ScreeningQuestionnaire : QuestionnaireBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_questionaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screeningQuestionnaire: StudyQuestionnaire =
            (activity as StudyFlowFragmentManager).getData(this) as StudyQuestionnaire

        configure(
            getString(R.string.questionnaire_segmentation),
            screeningQuestionnaire.instructions,
            screeningQuestionnaire.questions,
            this
        )

        button_questionnaire_next.setOnClickListener {
            if (!nextOnClick()) {
                lifecycleScope.launch {
                    progressBar_questionnaire.visibility = View.VISIBLE
                    button_questionnaire_next.isEnabled = false

                    val questionAnswersList = ArrayList<QuestionAnswer>()
                    questionAnswersList.addAll(questionAnswers)

                    sendData(questionAnswersList)
                }
            }
        }
    }

    private suspend fun sendData(questionAnswersList: ArrayList<QuestionAnswer>) {
        val json = Gson().toJson(questionAnswersList)

        val questionAnswersJson = JsonBuilder(
            "answers" to JSONArray(json)
        ).toJsonObject()

        var response: Any?

        try {
            response = UxMobile.adonisWebSocketClient.sendData(
                Constants.ADONIS_EVENT_SEND_ANSWERS,
                questionAnswersJson
            )
        } catch (e: Exception) {
//                button_questionnaire_next.isEnabled = true

//                val builder = activity.let { it1 ->
//                    androidx.appcompat.app.AlertDialog.Builder(
//                        it1!!,
//                        R.style.DialogTheme
//                    )
//                }
//                builder.setTitle(getString(R.string.plugin_name))
//                builder.setMessage(getString(R.string.error_no_internet_conn))
//                builder.setCancelable(false)
//
//                builder.setNegativeButton(getString(R.string.cancel_and_end_study)) { dialog, which ->
//                    dialog.cancel()
//                    GlobalScope.launch(Dispatchers.Main) {
//                        (activity as StudyFlowFragmentManager).showRejectedFragment()
//                    }
//                    return@setNegativeButton
//                }
//
//                builder.setPositiveButton(getString(R.string.try_again)) { dialog, which ->
//                    dialog.cancel()
//                    button_questionnaire_next.performClick()
//                    return@setPositiveButton
//                }
//
//                builder.show()
            Log.e(TAG, "Exception when sending data: " + e.cause + " " + e.message)
            GlobalScope.launch(Dispatchers.Main) {
                (activity as StudyFlowFragmentManager).showRejectedFragment()
            }
            return
        }

        // data received
        try {
            val jsonResponse = JSONObject(response.toString())

            when (jsonResponse.optString("event")) {
                Constants.ADONIS_EVENT_SEND_STUDY -> {
                    try {
                        val gson = Gson()
                        val study = gson.fromJson(
                            jsonResponse.optJSONObject("data").optJSONObject("data").toString(),
                            Study::class.java
                        )

                        StudyDataHolder.setNewStudy(study)

                        progressBar_questionnaire.visibility = View.GONE

                        (activity as StudyFlowFragmentManager).showNextFragment(this@ScreeningQuestionnaire)
                        return
                    } catch (e: Exception) {
                        Log.e(TAG, "Error: " + jsonResponse.optJSONObject("data").optString("error"))

                        val sender = StudyFlowController.sender

                        if (sender.isRunning) {
                            sender.stop()
                        }

                        GlobalScope.launch(Dispatchers.Main) {
                            (activity as StudyFlowFragmentManager).showRejectedFragment()
                        }
                        return
                    }
                }
                Constants.ADONIS_EVENT_QUIT -> {
                    val message = jsonResponse.optJSONObject("data").optJSONObject("data").getString("message")

                    StudyDataHolder.rejectMessage = message

                    GlobalScope.launch(Dispatchers.Main) {
                        (activity as StudyFlowFragmentManager).showRejectedFragment()
                    }
                    return
                }
                else -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        (activity as StudyFlowFragmentManager).showRejectedFragment()
                    }
                    return
                }
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Error occurred: " + e.message)
            (activity as StudyFlowFragmentManager).showRejectedFragment()

            val sender = StudyFlowController.sender

            if (sender.isRunning) {
                sender.stop()
            }
        }
    }
}
