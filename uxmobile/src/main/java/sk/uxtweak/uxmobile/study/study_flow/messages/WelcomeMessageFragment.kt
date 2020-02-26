package sk.uxtweak.uxmobile.study.study_flow.messages

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_message.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class WelcomeMessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val welcomeMessage: StudyMessage =
            (activity as StudyFlowFragmentManager).getData(this) as StudyMessage

        textView_message_title.text = welcomeMessage.title

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textVIew_message_content.text =
                Html.fromHtml(welcomeMessage.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textVIew_message_content.text = Html.fromHtml(welcomeMessage.content)
        }

        Handler().postDelayed({
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }, 3000)
    }
}
