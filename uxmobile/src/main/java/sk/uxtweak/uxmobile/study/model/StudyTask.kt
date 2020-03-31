package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Kamil Macek on 28. 1. 2020.
 */
data class StudyTask(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("starting_screen")
    val startingScreen: String,

    @JsonProperty("closing_screens")
    val closingScreens: List<String>,

    var accomplished: Boolean = false,
    var endedSuccessful: Boolean = false
)
