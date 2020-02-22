package sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_layouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_questionnaire_dropdown.*
import sk.uxtweak.uxmobile.R




/**
 * Created by Kamil Macek on 21.2.2020.
 */
class FragmentQuestionnaireOptionsDropdown: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_dropdown, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item, arrayOf("jedna", "dva", "tri")
            )
        }

        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_questionnaire.adapter = adapter
    }

}
