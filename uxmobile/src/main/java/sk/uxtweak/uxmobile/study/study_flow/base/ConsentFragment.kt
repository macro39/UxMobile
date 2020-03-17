package sk.uxtweak.uxmobile.study.study_flow.base

import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_consent.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager

/**
 * Created by Kamil Macek on 15. 12. 2019.
 */
class ConsentFragment : Fragment() {

    private val TAG = this::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_consent_terms.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        button_consent_terms.setOnClickListener {
            val builder = activity?.let { it1 -> AlertDialog.Builder(it1, R.style.DialogTheme) }
            builder?.setTitle(getString(R.string.plugin_name) + " - " + getString(R.string.terms_of_use))
            builder?.setMessage(Html.fromHtml(Constants.CONSENT_STRING))

            builder?.setNegativeButton(getString(R.string.no)) { dialog, which ->
                (activity as StudyFlowFragmentManager).showRejectedFragment()
                dialog.cancel()
            }

            builder?.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                (activity as StudyFlowFragmentManager).showNextFragment(this)
                dialog.cancel()
            }

            builder?.show()
        }

        button_consent_yes.setOnClickListener {
            (activity as StudyFlowFragmentManager).showNextFragment(this)
        }

        button_consent_no.setOnClickListener {
            Log.d(TAG, "User rejected study")
            (activity as StudyFlowFragmentManager).showRejectedFragment()
        }
    }
}
