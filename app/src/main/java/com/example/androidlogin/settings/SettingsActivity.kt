package com.example.androidlogin.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.androidlogin.R
import com.example.androidlogin.accounts.AccountUtils
import com.example.androidlogin.accounts.LoginActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Google SignIn
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

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

        FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.addOnCompleteListener {
            val provider = it.result?.signInProvider
            // Facebook Log Out
            if (AccountUtils.SOURCE_FACEBOOK == provider) {
                val loginManager = LoginManager.getInstance()
                loginManager.logOut()

                // Google sign Out
            } else if (AccountUtils.SOURCE_GOOGLE == provider) {
                googleSignInClient.signOut()
            }
        }

        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))

        finish()
    }
}
