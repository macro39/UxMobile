package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.fragment_questionnaire_checkbox.*
import sk.uxtweak.uxmobile.R


/**
 * Created by Kamil Macek on 21.2.2020.
 */
class QuestionnaireOptionsCheckbox : QuestionnaireOptionsBaseFragment(),
    CompoundButton.OnCheckedChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_checkbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configure()
    }

    override fun addOptions() {
        for (i in question.questionOptions.indices) {
            val checkBox = CheckBox(activity as Context?)

            checkBox.id = i
            checkBox.text = question.questionOptions[i].option

            if (i == 0) {
                checkBox.isChecked = true
                addAnswer(0)
            }

            checkBox.setOnCheckedChangeListener(this)
            checkbox_questionnaire_view.addView(checkBox)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            addAnswer(buttonView?.id!!)
        } else {
            removeAnswer(buttonView?.id!!)
        }
    }
}
