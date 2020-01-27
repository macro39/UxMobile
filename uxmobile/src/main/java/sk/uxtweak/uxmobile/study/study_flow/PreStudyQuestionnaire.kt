package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_pre_study_questionnaire.*
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 24. 1. 2020.
 */
class PreStudyQuestionnaire : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pre_study_questionnaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // dynamically set questions

        button_pre_study_questionnaire_next.setOnClickListener {
            (activity as StudyFlowFragment).showNextFragment(this)
        }
    }
}
