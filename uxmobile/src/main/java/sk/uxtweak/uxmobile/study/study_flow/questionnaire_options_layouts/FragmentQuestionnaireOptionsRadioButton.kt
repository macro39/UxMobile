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
import sk.uxtweak.uxmobile.study.study_flow.ScreeningQuestionnaireFragment

class FragmentQuestionnaireOptionsRadioButton : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_radio_button, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (i in 0..6) {
            val radioButton = RadioButton(view.context)
            radioButton.setTextColor(Color.BLACK)

            radioButton.isChecked = i == 0


            if (parentFragment is ScreeningQuestionnaireFragment) {
                radioButton.text = "screening questionnaire " + i
            } else {
                radioButton.text = "INY"
            }

            radioGroup_questionnaire.addView(radioButton)
        }
    }

}
