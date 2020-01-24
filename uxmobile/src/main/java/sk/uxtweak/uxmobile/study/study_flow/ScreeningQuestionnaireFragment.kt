package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_screening_questionaire.*
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class ScreeningQuestionnaireFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_screening_questionaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_screening_questionnaire_next.setOnClickListener {
            checkIfUserIsProper()
        }
    }

    // TODO add checker if questions are correctly answered, if not, then return process - this is only dummy
    private fun checkIfUserIsProper() {
        if (radioGroup_screening_questionnaire_sex.checkedRadioButtonId == R.id.radio_screening_questionnaire_male) {
            (activity as StudyFlowFragment).showNextFragment(this)
        } else {
            (activity as StudyFlowFragment).studyAccepted(false)
        }
    }
}
