package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 24. 1. 2020.
 */
class PreStudyQuestionnaire : QuestionnaireBaseFragment() {

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
                // TODO call server

                for (questionAnswer: QuestionAnswer in questionAnswers) {
                    Log.d(
                        "PreStudyQuestionnaire",
                        questionAnswer.id.toString() + " - " + questionAnswer.answers.toString()
                    )
                }

                (activity as StudyFlowFragmentManager).showNextFragment(this)
            }
        }
    }
}
