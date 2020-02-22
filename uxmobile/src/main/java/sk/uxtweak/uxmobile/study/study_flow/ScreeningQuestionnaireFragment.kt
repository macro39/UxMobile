package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.QuestionnaireRules
import sk.uxtweak.uxmobile.study.model.Question

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class ScreeningQuestionnaireFragment : Fragment() {

    companion object {
        lateinit var currentRule: Question
        var isSuitable = false
    }

    private lateinit var ruleToAnswers: MutableList<Question>
    private var ruleAnswering = 0
    private var totalRules = 0

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

        textView_question_title.text = questionnaireRules.title
        textView_question_description.text = questionnaireRules.description

        ruleToAnswers = questionnaireRules.rules.toMutableList()
        totalRules = questionnaireRules.rules.size

        showNextRule()


        button_questionnaire_next.setOnClickListener {
            if (!isSuitable) {
                (activity as StudyFlowFragmentManager).showRejectedFragment()
            } else {
                if (ruleToAnswers.size != 0) {
                    showNextRule()
                } else {
                    (activity as StudyFlowFragmentManager).showNextFragment(this)
                }
            }
        }
    }

    private fun showNextRule() {
        ruleAnswering++
        updateQuestionIndicator()
        val rule = ruleToAnswers.first()
        ruleToAnswers.removeAt(0)
        currentRule = rule
        (activity as StudyFlowFragmentManager).findProperQuestionType(this, rule.answerType)
    }

    private fun updateQuestionIndicator() {
        textView_question_indicator.text = "Question " + ruleAnswering + "/" + totalRules
    }
}
