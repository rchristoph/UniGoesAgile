package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class register : AppCompatActivity() {

    val nickname: String= ""
    val mAuth = FirebaseAuth.getInstance()
    lateinit var mDatabase : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)
        val registerBtn = findViewById<View>(R.id.nextBtn) as Button

        //Set var mDatabase to the right position in DB
        mDatabase = FirebaseDatabase.getInstance().getReference("Names")


        registerBtn.setOnClickListener(View.OnClickListener {
                view -> registerUser()
        })
    }

    private fun registerUser () {
        val emailTxt = findViewById<View>(R.id.emailTxt) as EditText
        val passwordTxt = findViewById<View>(R.id.passwordTxt) as EditText
        val nameTxt = findViewById<View>(R.id.nameTxt) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var name = nameTxt.text.toString()

        //Check if everything is filled out
        if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
            //Save new User in Database/Authentication
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val uid = user!!.uid
                    //Save new User with uid in realtime-database
                    mDatabase.child(uid).child("Name").setValue(name)
                    saveUserToFirebaseDatabase()
                    //User can now choose a project
                    startActivity(Intent(this, ChooseProject::class.java))
                    Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this, "Error registering, try again later :(", Toast.LENGTH_LONG).show()
                }
            })
        }else {
            Toast.makeText(this,"Please fill up the Credentials :|", Toast.LENGTH_LONG).show()
        }
    }


    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/Names/$uid")
        val user = User(uid, nameTxt.text.toString(), nickname.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("register", "Finally saved")
            }
    }
}

//! Content of Nickname will be stored later
class User(val uid:String, val name: String, val nickname: String)

