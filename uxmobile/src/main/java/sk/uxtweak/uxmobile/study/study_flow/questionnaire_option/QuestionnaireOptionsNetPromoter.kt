package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_questionnaire_net_promoter.*
import sk.uxtweak.uxmobile.R


/**
 * Created by Kamil Macek on 16.3.2020.
 */
class QuestionnaireOptionsNetPromoter : QuestionnaireOptionsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questionnaire_net_promoter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configure()

        indicator_seekBar_net_promoter.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                setText(seekParams.progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
            }
        }
    }

    override fun addOptions() {
        setText(indicator_seekBar_net_promoter.progress.toString())
    }
}
