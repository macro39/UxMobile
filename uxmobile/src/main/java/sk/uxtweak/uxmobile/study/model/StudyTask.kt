package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 28. 1. 2020.
 */
data class StudyTask(

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("starting_screen")
    val startingScreen: String,

    @SerializedName("closing_screens")
    val closingScreens: List<String>,

    var accomplished: Boolean = false
)
