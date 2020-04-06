package sk.uxtweak.uxmobile.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.debug_log_fragment.*
import sk.uxtweak.uxmobile.R

class DebugLogFragment : Fragment() {
    private val viewModel: DebugLogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.debug_log_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logArea.movementMethod = ScrollingMovementMethod.getInstance()

        viewModel.logChanged.observe(viewLifecycleOwner, Observer {
            logArea.append(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    companion object {
        const val TAG = "DebugLogFragmentTag"

        fun newInstance() = DebugLogFragment()
    }
}
