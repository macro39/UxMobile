package sk.uxtweak.uxmobile.study.study_flow

import java.io.Serializable

/**
 * Created by Kamil Macek on 13. 12. 2019.
 */
interface StudyFlowAcceptedObserver {
    fun studyAccepted(accepted: Boolean)
}
