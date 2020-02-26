package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
// TODO find out if this will be a part of task
class PostTaskQuestionnaire : QuestionnaireBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_questionaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question: StudyQuestionnaire =
            (activity as StudyFlowFragmentManager).getData(this) as StudyQuestionnaire

        textView_questionnaire_title.text = question.title
        textView_questionnaire_description.text = question.description

        button_questionnaire_next.setOnClickListener {
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }
    }
}
