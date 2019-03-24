package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_edit_task.*


class EditTask : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user!!.uid
    var projektIdent = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)


        database = FirebaseDatabase.getInstance().getReference("Projects")
        val _dbuser = FirebaseDatabase.getInstance().getReference("Names")

        val parentId = intent.getStringExtra("obID")
        val tDesc = intent.getStringExtra("tDesc")

        save_button.setOnClickListener(View.OnClickListener {
            saveTxt(parentId)
        })
        showTaskTitle.text = tDesc


        val new: ValueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val task = dataSnapshot.child("tasks/task").child(parentId)
                    if (task.exists()) {
                        val task2 = task.child("edit").getValue()

                        val task3 = task2.toString()
                        if (task3 != null) {
                            refreshView(task3)
                        } else {
                            newTextView.text = "Write your Userstory"
                        }
                    }


                }

            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }

        }

        var _projectListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                projektIdent = snapshot.value.toString()

                database.child(projektIdent).addValueEventListener(new)
            }



}
    _dbuser.child(uid).child("ProjektId").addValueEventListener(_projectListener)

    }






    private fun refreshView(task2: String){
            if (task2 != ""){
            newTextView.text= task2
            val storydesc = storyDescription
            storydesc.apply {
                setText(task2)
            }

            }
    }


    private fun saveTxt(parentId: String) {
                val storyText = findViewById<View>(R.id.storyDescription) as EditText
                var story = storyText.text.toString()
                database = FirebaseDatabase.getInstance().getReference("Projects")
                if (!story.isEmpty()) {
                    this.database.child(projektIdent).child("tasks/task").child(parentId).child("edit").setValue(story)
                }


            }

        }



          //  override fun onCancelled(databaseError: DatabaseError) {





