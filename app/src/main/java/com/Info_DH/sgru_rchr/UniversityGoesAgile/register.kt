package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.task_rows.*
import java.util.*

class register : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    lateinit var mDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBtn = findViewById<View>(R.id.regBtn) as Button


        mDatabase = FirebaseDatabase.getInstance().getReference("Names")


        registerBtn.setOnClickListener(View.OnClickListener {
                view -> registerUser()
        })

        select_photo_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        val adapter = ArrayAdapter.createFromResource(this,
            R.array.animal_arrays, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner1.adapter = adapter




    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!= null){

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }


    private fun registerUser () {
        val emailTxt = findViewById<View>(R.id.emailTxt) as EditText
        val passwordTxt = findViewById<View>(R.id.passwordTxt) as EditText
        val nameTxt = findViewById<View>(R.id.nameTxt) as EditText
        val nickName = findViewById<View>(R.id.spinner1) as Spinner

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var name = nameTxt.text.toString()
        var nick = nickName.selectedItem.toString()
        println("Das ist der Nickname: $nick")

        if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() &&nick!="Wähle ein Tier") {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val uid = user!!.uid
                    mDatabase.child(uid).child("Name").setValue(name)
                    startActivity(Intent(this, MainActivity::class.java))
                    //Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                    uploadImageToFirebaseStorage()
                }else {
                    Toast.makeText(this, "Error registering, try again later :(", Toast.LENGTH_LONG).show()
                }
            })
        }else {
            Toast.makeText(this,"Please fill up the Credentials :|", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("register", "Success: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                //do some logging here

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/Names/$uid")

        val user = User(uid, nameTxt.text.toString(), profileImageUrl, spinner1.selectedItem.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("register", "Finally saved")
            }
    }
}


class User(val uid:String, val username: String, val profileImageUrl:String, val nickName: String)

