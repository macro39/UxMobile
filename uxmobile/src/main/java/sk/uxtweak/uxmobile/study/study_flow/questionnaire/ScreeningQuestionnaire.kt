package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.QuestionnaireRules
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

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

        val questionnaireRules: QuestionnaireRules =
            (activity as StudyFlowFragmentManager).getData(this) as QuestionnaireRules

        configure(
            questionnaireRules.title,
            questionnaireRules.description,
            questionnaireRules.rules,
            this
        )

        button_questionnaire_next.setOnClickListener {
            if (!nextOnClick()) {
                // TODO call server to get study/default behaviour
                progressBar_questionnaire.visibility = View.VISIBLE
                (activity as StudyFlowFragmentManager).window.addFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                for (questionAnswer: QuestionAnswer in questionAnswers) {
                    Log.d(
                        "ScreeningQuestionnaire",
                        questionAnswer.id + " - " + questionAnswer.answers.toString()
                    )
                }

                Handler().postDelayed({
                    progressBar_questionnaire.visibility = View.GONE
                    (activity as StudyFlowFragmentManager).window.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    (activity as StudyFlowFragmentManager).showNextFragment(this)
                }, 1000)
            }
        }
    }
}
