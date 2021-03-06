package sk.uxtweak.uxmobile.study.float_widget

import android.view.View
import android.widget.Button
import android.widget.ImageView
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 24. 11. 2019.
 */
class FloatWidgetClickListener(
    mFloatView: View,
    private val listener: FloatWidgetClickObserver
) {

    private var isExpanded = false
    private var isOnRightSide = true

    // 2 states of float widget - collapsed(float button) and expanded(menu - instruction + end study)
    private var collapsedView: View = mFloatView.findViewById(R.id.collapsed_float_widget)
    private var expandedView: View = mFloatView.findViewById(R.id.expanded_float_widget)

    // 2 button for getting back to collapsed view - on left/right side depending on side of collapsed view
    private var backButtonLeft: ImageView =
        expandedView.findViewById(R.id.imageView_float_widget_back_left)
    private var backButtonRight: ImageView =
        expandedView.findViewById(R.id.imageView_float_widget_back_right)

    init {
        backButtonLeft.setOnClickListener {
            onClick(this.isOnRightSide)
        }

        backButtonRight.setOnClickListener {
            onClick(this.isOnRightSide)
        }

        expandedView.findViewById<Button>(R.id.button_float_widget_instructions)
            .setOnClickListener {
                listener.instructionClicked()
            }

        expandedView.findViewById<Button>(R.id.button_float_widget_end_task).setOnClickListener {
            listener.taskExecutionEnded(true)
        }

        expandedView.findViewById<Button>(R.id.button_float_widget_skip).setOnClickListener {
            listener.taskExecutionEnded(false)
        }
    }

    fun canMove(): Boolean {
        return !this.isExpanded
    }

    fun onClick(isOnRightSide: Boolean) {
        if (isExpanded) {
            makeViewCollapsed(isOnRightSide)
            return
        } else {
            makeViewExpanded(isOnRightSide)
            return
        }
    }

    fun makeViewCollapsed(isOnRightSide: Boolean) {
        this.isOnRightSide = isOnRightSide
        isExpanded = false

        collapsedView.visibility = View.VISIBLE
        expandedView.visibility = View.GONE

        return
    }

    fun makeViewExpanded(isOnRightSide: Boolean) {
        this.isOnRightSide = isOnRightSide
        isExpanded = true

        if (this.isOnRightSide) {
            backButtonRight.visibility = View.VISIBLE
            backButtonLeft.visibility = View.GONE
        } else {
            backButtonRight.visibility = View.GONE
            backButtonLeft.visibility = View.VISIBLE
        }

        collapsedView.visibility = View.GONE
        expandedView.visibility = View.VISIBLE

        return
    }
}
