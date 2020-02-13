package sk.uxtweak.uxmobile.study.study_flow

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_instructions.*
import kotlinx.android.synthetic.main.fragment_thank_you_message.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyMessage

/**
 * Created by Kamil Macek on 27. 1. 2020.
 */
class ThankYouMessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_thank_you_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thankYouMessage : StudyMessage = (activity as StudyFlowFragment).getData(this) as StudyMessage

        textView_thank_you_message_title.text = thankYouMessage.title

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView_thank_you_message_consent.text = Html.fromHtml(thankYouMessage.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView_thank_you_message_consent.text = Html.fromHtml(thankYouMessage.content)
        }

        Handler().postDelayed({
            (activity as StudyFlowFragment).showNextFragment(this)
        }, 3000)
    }
}
