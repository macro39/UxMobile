package sk.uxtweak.uxmobile.study.study_flow.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_task.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyTask
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
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

        (activity as StudyFlowFragmentManager).setLastVisibleElement(button_task_admit)

        val tasks = (activity as StudyFlowFragmentManager).getData(this) as List<StudyTask>

        val currentTask = tasks.first() { !it.accomplished }
        StudyDataHolder.doingTaskWithName = currentTask.name

        textView_task_name.text = currentTask.name
        textView_task_number.text =
            "Task " + (tasks.size - StudyDataHolder.numberOfTasks + 1) + "/" + tasks.size
        textView_task_description.text = currentTask.description

        button_task_admit.setOnClickListener {

            (activity as StudyFlowFragmentManager).studyAccepted(true)
        }
    }
}
