package sk.uxtweak.uxmobile.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.debug_fragment.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.UxMobile

class DebugFragment : Fragment() {
    private val viewModel: DebugFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.debug_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventRecorderSwitch.setOnClickListener { viewModel.eventRecorderClicked(eventRecorderSwitch.isChecked) }
        screenRecorderSwitch.setOnClickListener { viewModel.screenRecorderClicked(screenRecorderSwitch.isChecked) }
        connectionManagerSwitch.setOnClickListener { viewModel.connectionManagerClicked(connectionManagerSwitch.isChecked) }
        persisterSwitch.setOnClickListener { viewModel.persisterClicked(persisterSwitch.isChecked) }
        senderSwitch.setOnClickListener { viewModel.senderClicked(senderSwitch.isChecked) }

        viewModel.eventRecorderEnabled.observe(viewLifecycleOwner, Observer {
            eventRecorderSwitch.isChecked = it
        })

        viewModel.screenRecorderEnabled.observe(viewLifecycleOwner, Observer {
            screenRecorderSwitch.isChecked = it
        })

        viewModel.connectionManagerEnabled.observe(viewLifecycleOwner, Observer {
            connectionManagerSwitch.isChecked = it
        })

        viewModel.persisterEnabled.observe(viewLifecycleOwner, Observer {
            persisterSwitch.isChecked = it
        })

        viewModel.senderEnabled.observe(viewLifecycleOwner, Observer {
            senderSwitch.isChecked = it
        })

        logText.movementMethod = ScrollingMovementMethod.getInstance()
        viewModel.logArea.observe(viewLifecycleOwner, Observer {
            logText.text = it
        })

        regenerateSessionIdButton.setOnClickListener {
            UxMobile.sessionManager.generateSessionId()
        }
    }

    companion object {
        const val TAG = "DebugFragmentTag"

        fun newInstance() = DebugFragment()
    }
}
