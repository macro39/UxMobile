package sk.uxtweak.uxmobile.study.flow_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_global_message.*
import sk.uxtweak.uxmobile.R

class GlobalMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "UXMobile"
        setContentView(R.layout.activity_global_message)

        button_global_message_yes.setOnClickListener {
//            val intent = Intent(this, ConsentActivity::class.java)
//            startActivity(intent)
            finish()
        }

        button_global_message_no.setOnClickListener {
            finish()
        }

        button_global_message_later.setOnClickListener {
            // TODO
            finish()
        }

    }

    override fun onBackPressed() {
    }
}
