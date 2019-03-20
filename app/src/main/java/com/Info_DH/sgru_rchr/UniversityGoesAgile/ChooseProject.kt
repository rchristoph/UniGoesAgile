package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*

class ChooseProject : AppCompatActivity() {


    lateinit var _db: DatabaseReference
    lateinit var mDatabase: DatabaseReference
    var mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {

        _db = FirebaseDatabase.getInstance().getReference("Projects")
        mDatabase = FirebaseDatabase.getInstance().getReference("Names")

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose_project)


        val createButton = findViewById<View>(R.id.createBtn) as Button
        val joinButton = findViewById<View>(R.id.joinBtn) as Button

        //val projectID = findViewById<View>(R.id.projectTxt) as TextView




            createButton.setOnClickListener(View.OnClickListener {


                val projectID = findViewById<View>(R.id.projectTxt) as TextView
                var textfield = projectID.text.toString()
                if (!textfield.isEmpty()){
                createProject()
            }
                else {
                    Toast.makeText(this, "Namen eingeben! ;)", Toast.LENGTH_SHORT).show()
                }
                }
            )






    }

    private fun createProject(){
        val project = Project.create()
        val projectID = findViewById<View>(R.id.projectTxt) as TextView

        project.projectName = projectID.text.toString()
        //Get the object id for the new task from the Firebase Database



        val newProject = _db.push()
        project.objectId = newProject.key

        //Set the values for new Project in firebase
        newProject.setValue(project)


        //Aktuelles Projekt in als Child zum User hinzufügen
        val user = mAuth.currentUser
        val uid = user!!.uid
        mDatabase.child(uid).child("ProjektId").setValue(project.objectId)
        mDatabase.child(uid).child("ProjektName").setValue(project.projectName)



        Toast.makeText(this, "New Project created successfully " + project.objectId, Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, ToDoActivity::class.java))


    }


}









