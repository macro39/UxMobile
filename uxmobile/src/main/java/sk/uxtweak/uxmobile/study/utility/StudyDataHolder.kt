package sk.uxtweak.uxmobile.study.utility

import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.model.StudyTask

object StudyDataHolder {
    var numberOfTasks: Int = 0
    var doingTaskWithId = -1
    lateinit var tasks: List<StudyTask>
    var study: Study? = null


    fun getMessageData(type: String): StudyMessage {
        return study?.studyMessages?.first { p: StudyMessage -> p.type == type }!!
    }
}
