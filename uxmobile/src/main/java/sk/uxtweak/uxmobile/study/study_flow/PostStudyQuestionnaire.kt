package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
class PostStudyQuestionnaire : QuestionOptionsFragment() {

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
            question.title,
            question.description,
            question.questions,
            this
        )

        button_questionnaire_next.setOnClickListener {
            if (!nextOnClick()) {
                // TODO call server

                for (questionAnswer: QuestionAnswer in questionAnswers) {
                    Log.d(
                        "PostStudyQuestionnaire",
                        questionAnswer.id + " - " + questionAnswer.answers.asList().toString()
                    )
                }

                (activity as StudyFlowFragmentManager).showNextFragment(this)
            }
        }
    }
}
