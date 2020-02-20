package sk.uxtweak.uxmobile.study.utility

import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.model.StudyTask

object StudyDataHolder {
    var numberOfTasks: Int = 0
    var doingTaskWithId = -1
    lateinit var tasks: List<StudyTask>
    var study: Study? = null


    fun getBackgroundColorPrimary(): String {
        return study?.studyBrandings?.primaryColor!!
    }

    fun getBackgroundColorSecondary(): String {
        return study?.studyBrandings?.secondaryColor!!
    }


    fun getMessageData(type: String): StudyMessage {
        return study?.studyMessages?.first { p: StudyMessage -> p.type == type }!!
    }


    fun getQuestionData(type: String): StudyQuestion {
        return study?.studyQuestions?.first { p: StudyQuestion -> p.type == type }!!
    }
}
