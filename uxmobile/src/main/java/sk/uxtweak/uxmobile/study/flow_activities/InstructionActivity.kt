package sk.uxtweak.uxmobile.study.flow_activities

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.StudyFlowController
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MenuItem


class InstructionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "UXMobile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_instruction)


        val buttonNext = findViewById<Button>(R.id.button_instruction_next)

        buttonNext.setOnClickListener {
            super.onBackPressed()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        finish()
        return true
    }
}
