package sk.uxtweak.uxmobile.study.float_widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.flow_activities.InstructionActivity

/**
 * Created by Kamil Macek on 24. 11. 2019.
 */
class FloatWidgetClickListener(
    private val context: Context,       // only for toast, should be later deleted
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
                listener.instructionClicked(true)
            }

        expandedView.findViewById<Button>(R.id.button_float_widget_end_study).setOnClickListener {
            listener.studyStateChanged(false)
        }
    }

    fun canMove() : Boolean {
        return !this.isExpanded
    }

    fun onClick(isOnRightSide: Boolean) {
        this.isOnRightSide = isOnRightSide

        if (isExpanded) {
            isExpanded = false

            collapsedView.visibility = View.VISIBLE
            expandedView.visibility = View.GONE

            //TODO https://stackoverflow.com/questions/18147840/slide-right-to-left-android-animations

//            expandedView.animate()
//                .setDuration(800)
//                .translationX(expandedView.width.toFloat())
//                .setListener(object : AnimatorListenerAdapter() {
//                    override fun onAnimationStart(animation: Animator?) {
//                        expandedView.visibility = View.VISIBLE
//                    }
//                    override fun onAnimationEnd(animation: Animator?) {
//                        //super.onAnimationEnd(animation)
//                        expandedView.visibility = View.GONE
//                        collapsedView.visibility = View.VISIBLE
//                    }
//                })
//                .start()



//            expandedView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide))
//            expandedView.visibility = View.GONE

            //collapsedView.visibility = View.VISIBLE

            return
        } else {
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
}
