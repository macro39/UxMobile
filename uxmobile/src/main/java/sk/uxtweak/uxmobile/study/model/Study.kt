package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 6. 2. 2020.
 */
data class Study(

    @SerializedName("studyId")
    val studyId: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("studyBrandings")
    val studyBrandings: StudyBrandings,

    @SerializedName("studyTasks")
    val studyTasks: List<StudyTask>,

    @SerializedName("studyMessages")
    val studyMessages: List<StudyMessage>,

    @SerializedName("studyQuestions")
    val studyQuestions: List<StudyQuestion>
)
