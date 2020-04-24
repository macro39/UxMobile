package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 11. 2. 2020.
 */
data class StudyBrandings(

    @SerializedName("primary_color")
    val primaryColor: String = "#008570",

    @SerializedName("secondary_color")
    val secondaryColor: String = "#FFF57C00"
)
