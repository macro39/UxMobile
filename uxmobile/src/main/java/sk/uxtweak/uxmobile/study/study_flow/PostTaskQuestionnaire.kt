package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyQuestion

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
class PostTaskQuestionnaire : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_questionaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question: StudyQuestion = (activity as StudyFlowFragmentManager).getData(this) as StudyQuestion

        textView_question_title.text = question.title
        textView_question_description.text = question.description

        button_questionnaire_next.setOnClickListener {
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }
    }
}
