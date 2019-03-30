package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_nickname.*

class chooseNickname : AppCompatActivity() {

    lateinit var mDatabase : DatabaseReference
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nickname)


        val nextBtn = findViewById<View>(R.id.nextBtn)

        mDatabase = FirebaseDatabase.getInstance().getReference("Names")

        nextBtn.setOnClickListener(View.OnClickListener {
                view -> saveNickName()
        })

        val adapter = ArrayAdapter.createFromResource(this,
            R.array.animal_arrays, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner1.adapter = adapter

    }

    private fun saveNickName(){
        var nickNameTxt = findViewById<View>(R.id.spinner1) as Spinner
        val nickName = nickNameTxt.selectedItem.toString()

        if (nickName!="Wähle ein Tier"){
            val user = mAuth.currentUser
            val uid = user!!.uid
            mDatabase.child(uid).child("Name").child("Nickname").setValue(nickName)

            startActivity(Intent(this, MainActivity::class.java))


        }

    }

}
