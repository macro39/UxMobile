package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_consent.*
import sk.uxtweak.uxmobile.R

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

        button_consent_yes.setOnClickListener {
            (activity as StudyFlowFragment).showNextFragment(this)
        }

        button_consent_no.setOnClickListener {
            Log.d(TAG, "User rejected study")
            (activity as StudyFlowFragment).showRejectedFragment()
        }
    }
}
