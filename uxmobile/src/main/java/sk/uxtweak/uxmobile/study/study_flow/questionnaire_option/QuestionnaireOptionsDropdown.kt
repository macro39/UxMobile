package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_questionnaire_dropdown.*
import sk.uxtweak.uxmobile.R


/**
 * Created by Kamil Macek on 21.2.2020.
 */
class QuestionnaireOptionsDropdown : QuestionnaireOptionsBaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_dropdown, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configure()
    }

    override fun addOptions() {
        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                android.R.layout.simple_spinner_item, question.questionOptions.asList()
            )
        }

        setAnswer(0)

        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_questionnaire.adapter = adapter

        spinner_questionnaire.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setAnswer(position)
            }
        }
    }
}
