package sk.uxtweak.uxmobile.study.study_flow.messages

import kotlinx.android.synthetic.main.fragment_message.*
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
class ThankYouMessage : MessageBaseFragment() {

    override fun getData(): StudyMessage {
        return (activity as StudyFlowFragmentManager).getData(this) as StudyMessage
    }

    override fun buttonOnClick() {
        (activity as StudyFlowFragmentManager).showNextFragment(this)
    }
}
