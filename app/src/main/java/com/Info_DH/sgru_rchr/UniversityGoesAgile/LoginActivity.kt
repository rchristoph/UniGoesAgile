package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    //Verbindung zu Firebase Authentication
    var mAuth = FirebaseAuth.getInstance()
    val user = FirebaseAuth.getInstance().currentUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

            //Auto-Login or load view
            if (user != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                val loginBtn = findViewById<View>(R.id.loginBtn) as Button
                val registerTxt = findViewById<View>(R.id.regTxt) as TextView

                //Choose between login functionality and Register-Activity
                loginBtn.setOnClickListener(View.OnClickListener { view ->
                    login()
                })
                registerTxt.setOnClickListener{
                    startActivity(Intent(this, register::class.java))
                }
            }
    }


    private fun login () {
        //Save Input in variables
        val emailTxt = findViewById<View>(R.id.emailTxt) as EditText
        var email = emailTxt.text.toString()
        val passwordTxt = findViewById<View>(R.id.passwordTxt) as EditText
        var password = passwordTxt.text.toString()

        //Check if everything is filled out
        if (!email.isEmpty() && !password.isEmpty()) {
            this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener ( this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                   //Change activity to Mainactivity
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Successfully Logged in :)", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error Logging in :(", Toast.LENGTH_SHORT).show()
                }
            })

        }else {
            Toast.makeText(this, "Please fill up the Credentials :|", Toast.LENGTH_SHORT).show()
        }
    }
}