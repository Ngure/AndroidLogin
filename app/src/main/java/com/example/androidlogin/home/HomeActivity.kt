package com.example.androidlogin.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.androidlogin.R
import com.example.androidlogin.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        ab?.title = "Home"
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
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                // start your activity
                this.startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
