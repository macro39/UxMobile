package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.R

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class WelcomeMessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            (activity as StudyFlowFragment).studyAccepted(true)
        }, 3000)
    }

}
