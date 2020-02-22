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
import sk.uxtweak.uxmobile.study.model.Question
import sk.uxtweak.uxmobile.study.study_flow.ScreeningQuestionnaireFragment

class FragmentQuestionnaireOptionsRadioButton : Fragment() {

    private var rule: Question? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_radio_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (parentFragment) {
            is ScreeningQuestionnaireFragment -> {
                rule = ScreeningQuestionnaireFragment.currentRule

                for (i in rule!!.questionOptions.indices) {
                    val radioButton = RadioButton(view.context)
                    radioButton.id = i
                    radioButton.setTextColor(Color.BLACK)

                    if (i == 0) {
                        radioButton.isChecked = true
                        ScreeningQuestionnaireFragment.isSuitable = rule!!.questionOptions[0] in rule!!.ruleValues  // check if first - marked button is in rule
                    }

                    radioButton.text = rule!!.questionOptions[i]

                    radioGroup_questionnaire.addView(radioButton)
                }

                // if check for suitable respondent is on client side
                radioGroup_questionnaire.setOnCheckedChangeListener { group, checkedId ->
                    ScreeningQuestionnaireFragment.isSuitable = rule!!.questionOptions[checkedId] in rule!!.ruleValues
                }
            }
        }
    }

}
