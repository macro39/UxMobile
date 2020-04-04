package sk.uxtweak.uxmobile.study.study_flow.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_global_message.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 13. 12. 2019.
 */
class GlobalMessageFragment : Fragment() {

    private val TAG = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_global_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_global_message_yes.setOnClickListener {
            Log.d(TAG, "Going to next step in flow study")
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }

        button_global_message_no.setOnClickListener {
            Log.d(TAG, "User rejected study")
            (activity as StudyFlowFragmentManager).showRejectedFragment()
        }

        button_global_message_later.setOnClickListener {
            Log.d(TAG, "Ask later for study participating")
            (activity as StudyFlowFragmentManager).askLater()
        }
    }
}
