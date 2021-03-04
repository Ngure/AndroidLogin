package com.example.androidlogin.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidlogin.R
import com.example.androidlogin.accounts.LoginActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Call the setupUI() method
        setupUI()
    }

    /*
    * setupUI() method which will be used to set up the click listener for the sign out button.
    */
    private fun setupUI() {
        btn_log_out.setOnClickListener {
            logOut()
        }
    }

    /*
    * logOut() method which will be used to sign the user out of the application.
    */
    private fun logOut() {
        // Google sign Out
        FirebaseAuth.getInstance().signOut()
        // Facebook Log Out
        LoginManager.getInstance().logOut()

        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
    }
}
