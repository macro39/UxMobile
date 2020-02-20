package sk.uxtweak.uxmobile.study.study_flow

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_rejected_message.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.StudyMessage

/**
 * Created by Kamil Macek on 13. 2. 2020.
 */
class RejectedMessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rejected_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rejectedMessage: StudyMessage =
            (activity as StudyFlowFragment).getData(this) as StudyMessage

        textView_rejected_message_title.text = rejectedMessage.title

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textVIew_rejected_message_consent.text =
                Html.fromHtml(rejectedMessage.content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textVIew_rejected_message_consent.text = Html.fromHtml(rejectedMessage.content)
        }

        Handler().postDelayed({
            (activity as StudyFlowFragment).studyAccepted(false)
        }, 3000)
    }
}
