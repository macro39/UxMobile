package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_instructions.*
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 14. 12. 2019.
 */
class InstructionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO set instructions dynamically

        (activity as StudyFlowFragment).enableBackButton(true)

        button_instruction_next.setOnClickListener {
            //            (activity as StudyFlowFragment).enableBackButton(false)
            (activity as StudyFlowFragment).onBackPressed()
        }
    }
}
