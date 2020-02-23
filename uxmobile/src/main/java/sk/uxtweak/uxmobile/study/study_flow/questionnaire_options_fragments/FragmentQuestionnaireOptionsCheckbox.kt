package sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments

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
class FragmentQuestionnaireOptionsCheckbox : FragmentQuestionnaireBase(),
    CompoundButton.OnCheckedChangeListener {

    private lateinit var selectedOptions: ArrayList<String>

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

        selectedOptions = arrayListOf()
    }

    override fun addOptions() {
        for (i in question!!.questionOptions.indices) {
            val checkBox = CheckBox(activity)

            checkBox.id = i
            checkBox.text = question!!.questionOptions[i]

            checkBox.setOnCheckedChangeListener(this)
            checkbox_questionnaire_view.addView(checkBox)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            if (!selectedOptions.contains(question!!.questionOptions[buttonView?.id!!])) {
                selectedOptions.add(buttonView.text.toString())
            }
        } else {
            if (selectedOptions.contains(question!!.questionOptions[buttonView?.id!!])) {
                selectedOptions.remove(buttonView.text.toString())
            }
        }
    }

    fun getData(): List<String> {
        return selectedOptions
    }
}
