package sk.uxtweak.uxmobile.model.study

import org.json.JSONObject

data class Task(val id: Long, val title: String, val message: String) {
    companion object {
        @JvmStatic
        fun fromJson(jsonObject: JSONObject) = Task(
            jsonObject.getLong("task_id"),
            jsonObject.getString("title"),
            jsonObject.getString("message")
        )
    }
}
