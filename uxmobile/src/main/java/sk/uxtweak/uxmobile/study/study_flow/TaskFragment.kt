package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_task.*
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 24. 1. 2020.
 */
class TaskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO add tasks dynamically

        button_task_admit.setOnClickListener {
            (activity as StudyFlowFragment).studyAccepted(true)
        }

        button_task_refuse_executing.setOnClickListener {
            (activity as StudyFlowFragment).showNextFragment(this)
        }
    }
}
