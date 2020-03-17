package sk.uxtweak.uxmobile.study.study_flow.messages

import kotlinx.android.synthetic.main.fragment_message.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 13. 2. 2020.
 */
class RejectedMessage : MessageBaseFragment() {

    override fun getData(): StudyMessage {
        button_message_next.text = getString(R.string.end)
        return (activity as StudyFlowFragmentManager).getData(this) as StudyMessage
    }

    override fun buttonOnClick() {
        (activity as StudyFlowFragmentManager).studyAccepted(false)
    }
}
