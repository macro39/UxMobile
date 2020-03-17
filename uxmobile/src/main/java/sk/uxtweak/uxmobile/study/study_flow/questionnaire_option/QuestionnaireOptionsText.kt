package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_questionnaire_text.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants


/**
 * Created by Kamil Macek on 21.2.2020.
 */
class QuestionnaireOptionsText : QuestionnaireOptionsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configure()

        if (arguments?.getBoolean(Constants.EXTRA_IS_SINGLE_LINE)!!) {
            editText_questionnaire_text.isSingleLine = true
        }

        editText_questionnaire_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setText(editText_questionnaire_text.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    override fun addOptions() {
    }
}
