package sk.uxtweak.uxmobile.study.study_flow.messages

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_instructions.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

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

        (activity as StudyFlowFragmentManager).setLastVisibleElement(button_instruction_understand)

        val instructions: StudyMessage =
            (activity as StudyFlowFragmentManager).getData(this) as StudyMessage

        textView_instructions_title.text = instructions.title

        if ((activity as StudyFlowFragmentManager).isOnlyInstructionDisplayed()) {
            button_instruction_understand.text = getString(R.string.back)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView_instructions_content.text =
                Html.fromHtml(instructions.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView_instructions_content.text = Html.fromHtml(instructions.content)
        }

        button_instruction_understand.setOnClickListener {
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }
    }
}
