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

//        val radioGroup = getView()?.findViewById<RadioGroup>(R.id.radioGroup_tasks)

        val tasks = (activity as StudyFlowFragmentManager).getData(this) as List<StudyTask>

        val adapter = context?.let { context ->
            TaskAdapter(
                context,
                tasks.filter { !it.accomplished } as ArrayList<StudyTask>)
        }

        listView_task.adapter = adapter

        button_task_admit.setOnClickListener {
            StudyDataHolder.doingTaskWithName = adapter!!.getSelectedTask()
            (activity as StudyFlowFragmentManager).studyAccepted(true)
        }

        button_task_refuse_executing.setOnClickListener {
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }
    }
}
