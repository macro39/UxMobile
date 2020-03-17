package sk.uxtweak.uxmobile.study.study_flow.messages

import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class WelcomeMessage : MessageBaseFragment() {

    override fun getData(): StudyMessage {
        return (activity as StudyFlowFragmentManager).getData(this) as StudyMessage
    }

    override fun buttonOnClick() {
        (activity as StudyFlowFragmentManager).showNextFragment(this)
    }
}
