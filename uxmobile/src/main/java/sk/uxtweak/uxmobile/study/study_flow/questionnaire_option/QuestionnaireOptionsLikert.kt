package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import kotlinx.android.synthetic.main.fragment_questionnaire_likert.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants


/**
 * Created by Kamil Macek on 15.3.2020.
 */
class QuestionnaireOptionsLikert : QuestionnaireOptionsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_likert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!arguments?.getBoolean(Constants.EXTRA_IS_LIKERT_7)!!) {
            radioGroup_likert.orientation = LinearLayout.HORIZONTAL

            radioButton_more_or_less_agree.visibility = View.GONE
            radioButton_more_or_less_disagree.visibility = View.GONE
        }

        configure()

        radioGroup_likert.setOnCheckedChangeListener { group, checkedId ->
            setText(group.findViewById<RadioButton>(checkedId).text.toString())
        }
    }

    override fun addOptions() {
        setText(radioButton_neutral.text.toString())
    }
}
