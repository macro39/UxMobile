package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_post_study_questionnaire.*
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
class PostStudyQuestionnaire : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_study_questionnaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_post_study_questionnaire_next.setOnClickListener {
            (activity as StudyFlowFragment).showNextFragment(this)
        }
    }
}
