package sk.uxtweak.uxmobile.study.study_flow

import android.graphics.Color
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
import sk.uxtweak.uxmobile.study.model.StudyTask
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

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

        val radioGroup = getView()?.findViewById<RadioGroup>(R.id.radioGroup_tasks)

        val tasks = (activity as StudyFlowFragment).getData(this) as List<StudyTask>


        var marked = false
        for (studyTask : StudyTask in tasks) {

            if (studyTask.accomplished) {
                continue
            }

            val radioButton = RadioButton(activity)
            radioButton.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.text = studyTask.title
            radioButton.setTextColor(Color.BLACK)
            radioButton.text
            radioButton.id = studyTask.taskId.toInt()

            if (!marked) {
                radioButton.isChecked = true
                marked = true
            }

            radioGroup?.addView(radioButton)
        }

        button_task_admit.setOnClickListener {
            StudyDataHolder.doingTaskWithId = radioGroup?.checkedRadioButtonId!!
            (activity as StudyFlowFragment).studyAccepted(true)
        }

        button_task_refuse_executing.setOnClickListener {
            (activity as StudyFlowFragment).showNextFragment(this)
        }
    }
}
