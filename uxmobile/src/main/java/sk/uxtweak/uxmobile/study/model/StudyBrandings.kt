package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 11. 2. 2020.
 */
data class StudyBrandings(

    @SerializedName("primaryColor")
    val primaryColor: String,

    @SerializedName("secondaryColor")
    val secondaryColor: String
)
