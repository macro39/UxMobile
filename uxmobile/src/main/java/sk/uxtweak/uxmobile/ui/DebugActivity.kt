package sk.uxtweak.uxmobile.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.debug_layout.*
import sk.uxtweak.uxmobile.R

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debug_layout)
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, DebugFragment.newInstance(), DebugFragment.TAG)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.debug_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuClose -> finish()
            R.id.menuLog -> {
                supportFragmentManager.commit {
                    replace(R.id.fragmentContainer, DebugLogFragment.newInstance(), DebugLogFragment.TAG)
                    addToBackStack(DebugLogFragment.TAG)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
