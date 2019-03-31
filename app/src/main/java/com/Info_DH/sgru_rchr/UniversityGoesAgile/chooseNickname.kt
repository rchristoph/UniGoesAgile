package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_nickname.*

class chooseNickname : AppCompatActivity() {

    lateinit var mDatabase : DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    var newItem: String = ""
    var cities = arrayOf("Wähle eine Stadt", "Bangkok", "London", "Paris", "Sydney", "New York", "Istanbul", "Dubai", "Hanoi", "Hongkong", "Barcelona")
    var animals = arrayOf("Wähle ein Tier", "Bär", "Giraffe", "Eule", "Löwe", "Kranich", "Wiesel", "Okapi", "Wolf", "Robbe")
    var politic = arrayOf("Wähle PolitikerIn", "Merkel", "Obama", "May", "Macron", "Putin", "Orban", "Solberg", "Arden", "Abe")
    var history = arrayOf("Wähle eine Epoche", "Steinzeit", "Klassik", "Moderne", "Realismus", "Antike", "Aufklärung", "Renaissance")
    var strings = arrayOf("")
    val user = mAuth.currentUser
    val uid = user!!.uid



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nickname)

        val projectID = intent.getStringExtra("projectID")
        println("")

        val nextBtn = findViewById<View>(R.id.nextBtn)

        mDatabase = FirebaseDatabase.getInstance().getReference("Names")


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, animals)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner1.adapter = adapter

        nextBtn.setOnClickListener(View.OnClickListener { view ->
            saveNickName(projectID)
        })


    }


    private fun saveNickName(projectID: String){
        var nickNameTxt = findViewById<View>(R.id.spinner1) as Spinner
        val nickName = nickNameTxt.selectedItem.toString()

        if (!nickName.isEmpty()){
            val user = mAuth.currentUser
            val uid = user!!.uid
            mDatabase.child(uid).child("Name").child("Nickname").setValue(nickName)

            startActivity(Intent(this, MainActivity::class.java))

        }

    }





}

