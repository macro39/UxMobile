package sk.uxtweak.uxmobile.model.event

import sk.uxtweak.uxmobile.model.ViewEnum

class LongPressEvent(
    startTime: Long,
    x: Float,
    y: Float,
    viewEnum: ViewEnum,
    viewText: String,
    viewValue: String
) : ViewEvent(startTime, x, y, viewEnum, viewText, viewValue) {
    override fun getType() = TYPE_LONG_PRESS
}
