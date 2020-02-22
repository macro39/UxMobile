package sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_layouts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_questionnaire_radio_button.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.study_flow.ScreeningQuestionnaireFragment

class FragmentQuestionnaireOptionsRadioButton : Fragment() {

    private var question: StudyQuestion? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_radio_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        question = FragmentQuestionOption.currentQuestion

        addOptionsToRadioButtons()

        radioGroup_questionnaire.setOnCheckedChangeListener { group, checkedId ->
            updateQuestionnaireAnswers(checkedId)
        }
    }

    private fun updateQuestionnaireAnswers(checkedId: Int) {
        when (parentFragment) {
            is ScreeningQuestionnaireFragment -> {
                FragmentQuestionOption.addQuestionAnswer(
                    QuestionAnswer(
                        question!!.id,
                        listOf(question!!.questionOptions[checkedId]).toTypedArray()
                    )
                )
            }
        }
    }

    private fun addOptionsToRadioButtons() {
        for (i in question!!.questionOptions.indices) {
            val radioButton = RadioButton(activity)
            radioButton.id = i
            radioButton.setTextColor(Color.BLACK)

            if (i == 0) {
                radioButton.isChecked = true
                updateQuestionnaireAnswers(i)
            }

            radioButton.text = question!!.questionOptions[i]

            radioGroup_questionnaire.addView(radioButton)
        }
    }

}
