package com.example.recipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {


    private lateinit var mAuth : FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN: Int = 9001

    private val mHandler: Handler = Handler()
    var count = 0

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser != null){
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googlesetting()
        login_btn_google_Login.setOnClickListener {
            loginbygoogle()
        }

        mToastRunnable.run()

    }

    private val mToastRunnable: Runnable = object : Runnable {
        override fun run() {
            if (count == 0){
                login_txt_msg.text = "Im just lazy"
            }else if(count == 1){
                login_txt_msg.text = "Im just lazy."
            }else if(count == 2){
                login_txt_msg.text = "Im just lazy.."
            }else if(count == 3){
                login_txt_msg.text = "Im just lazy..."

            }

            if(count == 3){
                count = 0
            }else{
                count++
            }

            mHandler.postDelayed(this, 1000)
        }
    }



    //AOuth
    private fun googlesetting() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()


    }

    private fun loginbygoogle(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this,"Google sign in failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val i = Intent(this,MainActivity::class.java)
                    finish()
                    startActivity(i)
                }


            }
    }
    /////


}