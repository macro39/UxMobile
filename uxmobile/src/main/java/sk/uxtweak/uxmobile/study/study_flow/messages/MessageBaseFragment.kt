package sk.uxtweak.uxmobile.study.study_flow.messages

import `in`.uncod.android.bypass.Bypass
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_message.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager


/**
 * Created by Kamil Macek on 28.2.2020.
 */
abstract class MessageBaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as StudyFlowFragmentManager).setLastVisibleElement(button_message_next)

        val message: StudyMessage = getData()

        textView_message_title.text = message.title

        textView_message_text.text = Bypass().markdownToSpannable(message.content)

        button_message_next.setOnClickListener {
            buttonOnClick()
        }
    }

    abstract fun getData(): StudyMessage
    abstract fun buttonOnClick()
}
