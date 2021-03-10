package com.example.androidlogin.accounts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlogin.R
import com.example.androidlogin.home.HomeActivity
import com.example.androidlogin.utils.LogUtils
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: View
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleLoginButton: Button
    private lateinit var facebookLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        googleLoginButton = btn_google_login
        facebookLoginButton = btn_facebook_login

        progressBar = include_progress_bar

        googleLoginButton.setOnClickListener { googleLogin() }

        // Configure Google SignIn
        configureGoogleSignIn()

        callbackManager = CallbackManager.Factory.create()

        facebookLoginButton.setOnClickListener { facebookLogin() }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        }
    }

    private fun configureGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, googleSignInOptions)
    }

    private fun facebookLogin() {
        progressBar.visibility = View.VISIBLE

        val loginManager = LoginManager.getInstance()
        loginManager.logInWithReadPermissions(this@LoginActivity, listOf("email", "public_profile"))

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                finishWithAuthCredential(credential)
            }

            override fun onCancel() {
                progressBar.visibility = View.GONE
                LogUtils.LOGD(TAG, "Facebook: User cancelled")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            override fun onError(error: FacebookException) {
                progressBar.visibility = View.GONE
                LogUtils.LOGE(TAG, "Facebook: Error", error)
                finish()
            }
        })
    }

    private fun googleLogin() {
        progressBar.visibility = View.VISIBLE

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // handle Facebook login result
        callbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                LogUtils.LOGW(TAG, "Google sign in failed", e)
                Snackbar.make(
                    login_activity_layout,
                    "Google sign in failed.",
                    Snackbar.LENGTH_SHORT
                ).show()
                // Add SHA fingerprint if error persist ;-)

                progressBar.visibility = View.GONE
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        LogUtils.LOGD(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        progressBar.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    LogUtils.LOGD(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    LogUtils.LOGW(TAG, "signInWithCredential:failure", task.exception!!)
                    Snackbar.make(
                        login_activity_layout,
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
                progressBar.visibility = View.GONE
            }
    }

    private fun finishWithAuthCredential(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                LogUtils.LOGD(TAG, "signIn with credentials successful")
                val user = task.result!!.user
                if (user != null) {
                    // check if the email address is valid, and if not we cannot proceed
                    if (!isEmailAddressValid(user.email)) {
                        setResult(AccountUtils.LOGIN_RESULT_CODE_INVALID_EMAIL)
                        finish()
                        return@addOnCompleteListener
                    }

                    val intent = Intent()
                    setResult(AccountUtils.LOGIN_RESULT_CODE_OK, intent)
                }

                updateUI(user)
                finish()
            } else {
                val exception = task.exception

                if (exception is FirebaseAuthUserCollisionException) {
                    setResult(AccountUtils.LOGIN_RESULT_CODE_ACCOUNT_ALREADY_EXISTS)
                    return@addOnCompleteListener
                }

                setResult(AccountUtils.LOGIN_RESULT_CODE_PROFILES_FAILED)
            }
        }
    }

    private fun isEmailAddressValid(emailAddress: String?): Boolean {
        if (TextUtils.isEmpty(emailAddress))
            return false
        if (!emailAddress!!.contains("@") || !emailAddress.contains("."))
            return false

        val iat = emailAddress.indexOf("@")
        val dat = emailAddress.lastIndexOf(".")
        return dat > iat
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private val TAG = LogUtils.makeLogTag(LoginActivity::class.java)

        //Request code
        const val RC_GOOGLE_SIGN_IN = 9001
    }
}