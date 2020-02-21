package sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_layouts

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_questionnaire_text.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants


/**
 * Created by Kamil Macek on 21.2.2020.
 */
class FragmentQuestionnaireOptionsText: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getBoolean(Constants.EXTRA_IS_SINGLE_LINE)!!) {
            textView_questionnaire_text.isSingleLine = true
        }
    }

    fun getData(): String {
        return textView_questionnaire_text.text.toString()
    }
}
