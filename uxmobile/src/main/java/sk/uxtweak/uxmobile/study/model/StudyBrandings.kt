package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Kamil Macek on 11. 2. 2020.
 */
data class StudyBrandings(

    @JsonProperty("primary_color")
    val primaryColor: String,

    @JsonProperty("secondary_color")
    val secondaryColor: String
)
