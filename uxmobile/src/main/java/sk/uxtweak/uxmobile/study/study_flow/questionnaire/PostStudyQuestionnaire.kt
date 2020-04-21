package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.StudyFlowController
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.net.JsonBuilder
import sk.uxtweak.uxmobile.study.persister.QuestionAnswerEntity
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.toJson

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
class PostStudyQuestionnaire : QuestionnaireBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_questionaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question: StudyQuestionnaire =
            (activity as StudyFlowFragmentManager).getData(this) as StudyQuestionnaire

        configure(
            question.name,
            question.instructions,
            question.questions,
            this
        )

        button_questionnaire_next.setOnClickListener {
            if (!nextOnClick()) {
                lifecycleScope.launch {
                    val json = Gson().toJson(questionAnswers)

                    val questionAnswersJson = JsonBuilder(
                        "answers" to JSONArray(json)
                    ).toJsonObject()

                    Log.d(TAG, "QUESTION ANSWERS SAVING TO LOCAL DB $questionAnswersJson")

                    val questionAnswerEntity =
                        QuestionAnswerEntity(id = 0, questionAnswer = questionAnswersJson.toString())

                    StudyFlowController.database.questionAnswerDao()
                        .insert(listOf(questionAnswerEntity))

                    (activity as StudyFlowFragmentManager).showNextFragment(this@PostStudyQuestionnaire)
                }
            }
        }
    }
}
