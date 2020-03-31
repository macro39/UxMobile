package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

/**
 * Created by Kamil Macek on 19. 1. 2020.
 */
class ScreeningQuestionnaire : QuestionnaireBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_questionaire, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screeningQuestionnaire: StudyQuestionnaire =
            (activity as StudyFlowFragmentManager).getData(this) as StudyQuestionnaire

        configure(
            screeningQuestionnaire.name,
            screeningQuestionnaire.instructions,
            screeningQuestionnaire.questions,
            this
        )

        button_questionnaire_next.setOnClickListener {
            if (!nextOnClick()) {
                // TODO call server to get study/default behaviour
                progressBar_questionnaire.visibility = View.VISIBLE
                (activity as StudyFlowFragmentManager).window.addFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                for (questionAnswer: QuestionAnswer in questionAnswers) {
                    Log.d(
                        "ScreeningQuestionnaire",
                        questionAnswer.id.toString() + " - " + questionAnswer.answers.toString()
                    )
                }

                Handler().postDelayed({
                    StudyDataHolder.setNewStudy(getStudy())

                    progressBar_questionnaire.visibility = View.GONE
                    (activity as StudyFlowFragmentManager).window.clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    (activity as StudyFlowFragmentManager).showNextFragment(this)
                }, 1000)
            }
        }
    }

    fun getStudy(): Study {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(dummyResponseData(), Study::class.java)
    }

    private fun dummyResponseData(): String {
        return "{\n" +
            "    \"name\":\"Study 1\",\n" +
            "    \"id\":\"1\",\n" +
            "    \"closing_rule\":\"string\",\n" +
            "    \"closing_date\":122,\n" +
            "    \"respondent_limit\":100,\n" +
            "    \"welcome_message\":\"Welcome to this study, and thank you for agreeing to participate! The activity shouldn't take longer than 30 to 60 minutes to complete. Your response will help us to better understand how people behave in our app.\",\n" +
            "    \"introduction\":\"<b>Here's how it works:</b><ol><li>You will be presented with a task.</li><li>After reading the task, you will be redirected to a website.</li><li>Click through the website as you naturally would in order to fulfill the task.</li><li>Once you arrive at the intended destination, click <b>Task done</b> and the task will end.</li><li>Repeat the previous steps for all the studyTasks to complete the RePlay study.</li></ol><em>This is not a test of your ability, there are no right or wrong answers.</em><br><b>That's it, let's get started!</b></div>\",\n" +
            "    \"pre_study_questionnaire\":{\n" +
            "        \"name\":\"PRE STUDY QUESTIONNAIRE\",\n" +
            "        \"instructions\":\"Please fill this questionnaire\",\n" +
            "        \"questions\":[\n" +
            "            {\n" +
            "                \"id\":\"1\",\n" +
            "                \"name\":\"What's your gender?\",\n" +
            "                \"question_required\":true,\n" +
            "                \"description\":\"Have you ever been working on study?\",\n" +
            "                \"answer_type\":\"radio_button\",\n" +
            "                \"answer_required\":true,\n" +
            "                \"reason_needed\":false,\n" +
            "                \"question_options\":[\n" +
            "                    {\n" +
            "                        \"id\":\"1\",\n" +
            "                        \"option\":\"yes\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"id\":\"2\",\n" +
            "                        \"option\":\"no\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"task_list\":[\n" +
            "        {\n" +
            "            \"name\":\"Task n.1\",\n" +
            "            \"description\":\"MARK WHEN YOU HAVE BIRTHDAY\",\n" +
            "            \"starting_screen\":\"FILL\",\n" +
            "            \"closing_screens\":[\n" +
            "                \"FILL\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"name\":\"Task n.2\",\n" +
            "            \"description\":\"CREATE NEW APPOINTMENT ON 27.05.2022\",\n" +
            "            \"starting_screen\":\"FILL\",\n" +
            "            \"closing_screens\":[\n" +
            "                \"FILL\"\n" +
            "            ]\n" +
            "        }\n" +
            "    ],\n" +
            "    \"post_study_questionnaire\":{\n" +
            "        \"name\":\"POST STUDY QUESTIONNAIRE\",\n" +
            "        \"instructions\":\"Please fill this questionnaire\",\n" +
            "        \"questions\":[\n" +
            "            {\n" +
            "                \"id\":\"1\",\n" +
            "                \"name\":\"What's your gender?\",\n" +
            "                \"question_required\":true,\n" +
            "                \"description\":\"Were tasks hard to complete?\",\n" +
            "                \"answer_type\":\"5_point_linker_scale\",\n" +
            "                \"answer_required\":true,\n" +
            "                \"reason_needed\":false\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"thank_you_message\":\"All done, awesome! Thanks again for your participation. Your feedback is incredibly useful in helping us understand how people interact with our app, so that we can make our application easier to use. You may now going back to your work!\",\n" +
            "    \"study_branding\":{\n" +
            "        \"primary_color\":\"#008570\",\n" +
            "        \"secondary_color\":\"#FFF57C00\"\n" +
            "    }\n" +
            "}"
    }
}
