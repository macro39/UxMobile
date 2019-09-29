package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray
import sk.uxtweak.uxmobile.model.ViewEnum

abstract class ViewEvent(
    startTime: Long,
    x: Float,
    y: Float,
    private val viewEnum: ViewEnum,
    private val viewText: String,
    private val viewValue: String
) : TouchEvent(startTime, x, y) {
    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_VIEW, viewEnum.jsonValue)
        .put(INDEX_VIEW_TEXT, viewText)
        .put(INDEX_VIEW_VALUE, viewValue)

    companion object {
        const val INDEX_VIEW = 4
        const val INDEX_VIEW_TEXT = 5
        const val INDEX_VIEW_VALUE = 6
    }
}
