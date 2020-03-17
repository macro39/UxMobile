package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.fragment_questionnaire_radio_button.*
import sk.uxtweak.uxmobile.R


/**
 * Created by Kamil Macek on 21.2.2020.
 */
class QuestionnaireOptionsRadioButton : QuestionnaireOptionsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_radio_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configure()

        radioGroup_questionnaire.setOnCheckedChangeListener { group, checkedId ->
            setAnswer(checkedId)
        }
    }

    override fun addOptions() {
        for (i in question.questionOptions.indices) {
            val radioButton = RadioButton(activity)
            radioButton.id = i
            radioButton.setTextColor(Color.BLACK)

            if (i == 0) {
                radioButton.isChecked = true
                setAnswer(i)
            }

            radioButton.text = question.questionOptions[i].option

            radioGroup_questionnaire.addView(radioButton)
        }
    }

}
