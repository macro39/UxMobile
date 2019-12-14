package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_global_message.*
import sk.uxtweak.uxmobile.R

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
        return inflater.inflate(R.layout.activity_global_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_global_message_yes.setOnClickListener {
            Log.d(TAG, "User is in study")

//            (activity as StudyFlowFragmentController).StudyFlowFragment().studyAccepted(true)
            (activity as StudyFlowFragment).studyAccepted(true)
        }

        button_global_message_no.setOnClickListener {
            Log.d(TAG, "User rejected study")
//            (activity as StudyFlowFragmentController).StudyFlowFragment().studyAccepted(false)
            (activity as StudyFlowFragment).studyAccepted(false)
        }

        button_global_message_later.setOnClickListener {
            Log.d(TAG, "Ask later for study participating")
//            (activity as StudyFlowFragmentController).StudyFlowFragment().askLater(true)
            (activity as StudyFlowFragment).askLater(false)
        }

    }
}
