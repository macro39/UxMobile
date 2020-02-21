package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 21.2.2020.
 */
data class QuestionnaireRules(

    @SerializedName("description")
    val description: String,

    @SerializedName("rules")
    val rules: List<Rule>
)
