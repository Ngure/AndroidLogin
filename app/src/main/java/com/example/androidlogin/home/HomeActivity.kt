package com.example.androidlogin.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlogin.R
import com.example.androidlogin.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        title = "Home"

        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        setSupportActionBar(mainToolbar)

        val extras = intent.extras
        if (extras != null) {
            val value = extras.getString("key")
            //The key argument here must match that used in the other activity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.toolbar_settings -> {
                val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
                // start your activity
                this.startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
