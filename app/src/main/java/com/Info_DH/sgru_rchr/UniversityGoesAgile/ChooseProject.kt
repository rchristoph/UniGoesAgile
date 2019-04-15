package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_project.*
import java.util.*

class ChooseProject : AppCompatActivity() {


    lateinit var _db: DatabaseReference
    lateinit var mDatabase: DatabaseReference
    var mAuth = FirebaseAuth.getInstance()
    var status: String = "false"




    override fun onCreate(savedInstanceState: Bundle?) {

        //Referenz zu der Firebase Datenbank wird in einer Variable gespeichert
        //Referenz zum Child-Node in dem die User gespeichert sind
        _db = FirebaseDatabase.getInstance().getReference("Projects")
        mDatabase = FirebaseDatabase.getInstance().getReference("Names")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_project)

        //Layout Switch: Neues Projekt vs. bestehendem Projekt beitreten
        changeActivity.setOnClickListener {
            textInputLayout4.visibility = View.GONE
            joinBtn.visibility = View.GONE
            changeActivity.visibility = View.GONE
            changeActivity.visibility = View.GONE
            joinProj.visibility = View.GONE

            ddl.visibility = View.VISIBLE
            joinExist.visibility = View.VISIBLE
            projectTxt.visibility = View.VISIBLE
            chooseProj.visibility = View.VISIBLE
            createBtn.visibility = View.VISIBLE
            showDate.visibility = View.VISIBLE
            datePicker.visibility = View.VISIBLE
            nickname.visibility = View.VISIBLE
        }

        joinExist.setOnClickListener {
            textInputLayout4.visibility = View.VISIBLE
            joinBtn.visibility = View.VISIBLE
            changeActivity.visibility = View.VISIBLE
            changeActivity.visibility = View.VISIBLE
            joinProj.visibility = View.VISIBLE

            ddl.visibility = View.GONE
            joinExist.visibility = View.GONE
            projectTxt.visibility = View.GONE
            chooseProj.visibility = View.GONE
            createBtn.visibility = View.GONE
            showDate.visibility = View.GONE
            datePicker.visibility = View.GONE
            nickname.visibility = View.GONE
        }


        //Calendar vars+method
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

            datePicker.setOnClickListener {
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{view, mYear, mMonth, mDay ->

                    showDate.setText(""+ mDay +"/"+ (mMonth+1) +"/"+ mYear)
                }, year, month, day)

                dpd.show()

            }

        //Variable für Buttons
        val createButton = findViewById<View>(R.id.createBtn) as Button
        val joinButton = findViewById<View>(R.id.joinBtn) as Button

        //An einem vorhandenen Projekt teilnehmen
            joinButton.setOnClickListener(View.OnClickListener {
                val projectId = findViewById<View>(R.id.projectid) as TextView
                val textfield = projectId.text.toString()
                if (!textfield.isEmpty()) {
                    joinProject()
                }
            })

        //Neues Projekt wird angelegt
            createButton.setOnClickListener(View.OnClickListener {
                val projectID = findViewById<View>(R.id.projectTxt) as TextView
                var textfield = projectID.text.toString()
                if (!textfield.isEmpty()){
                createProject()
                }
                else {
                    Toast.makeText(this, "Namen eingeben! ;)", Toast.LENGTH_SHORT).show()
                }
                })
    }

    //An Projekt teilnehmen:
    private fun joinProject(){

        //Aktuelles Projekt in als Child zum User hinzufügen
        var projectID = findViewById<View>(R.id.projectid) as TextView
        val user = mAuth.currentUser
        val uid = user!!.uid
        mDatabase.child(uid).child("ProjektId").setValue(projectID.text.toString())
        val newValue = _db.child(projectID.text.toString()).child("members")
        val nName = Members("")
        newValue.child(uid).setValue(nName)

        //hier muss ich jetzt prüfen ob der User im Projekt schon als Member dabei ist. Wenn ja kann das Projekt direkt aufgerufen werden. Wenn nicht muss ich erstmal einen Nickname auswählen

        var _mmber = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                println("Das ist mein SnapshotNeu:${p0.children}")
                println("Das sind meine Snapshots:${p0.child("members").value}")
                var findMember = p0.child("members")
                println("Member1:$findMember")
                if (findMember!!.exists()) {
                    for (h in findMember.children) {
                        var member = h.getValue(Members::class.java).toString()
                        println("Hier sind meine Member:$member")
                        }
                    }
                }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        _db.child(projectID.text.toString()).addValueEventListener(_mmber)

        val prjctID = projectID.text.toString()
        println("Das ist der prjctID-String: $prjctID")

        val dataBaseEntry = mDatabase.child(uid).child("Projekte")
        val newPrjct = ProjectShort (prjctID)
        dataBaseEntry.child(prjctID).setValue(newPrjct)
        status = "false"

        val intent = Intent(this@ChooseProject, chooseNickname::class.java)
        intent.putExtra("status", status)
        startActivity(intent)
    }

    private fun createProject(){

        // An dieser Stelle wird die Klasse "Project" verknüpft
        val project = Project.create()
        val projectID = findViewById<View>(R.id.projectTxt) as TextView

        //Übergebe die vom User eingegebenen Daten an die Klasse
        project.projectName = projectID.text.toString()
        project.deadLine = showDate.text.toString()
        project.theme = "leer"

        //Schreibe die Daten in die DB
        val newProject = _db.push()
        project.objectId = newProject.key

        //Set the values for new Project in firebase
        newProject.setValue(project)


        //Aktuelle Projekt-ID als Child zum User in der DB hinzufügen
        val user = mAuth.currentUser
        val uid = user!!.uid
        mDatabase.child(uid).child("ProjektId").setValue(project.objectId)
        mDatabase.child(uid).child("ProjektName").setValue(project.projectName)
        val pro = mDatabase.child(uid).child("Projekte")
        pro.setValue(project.objectId)


        //Lege die Struktur Tasks/Task an. Dort werden später die Aufgaben gespeichert
        _db.child(project.objectId.toString()).child("tasks").child("task").setValue("")
        val newValue = _db.child(project.objectId.toString()).child("members")
        val nName = Members("")
        newValue.child(uid).setValue(nName)
        newValue.child(uid).setValue(nName)

        Toast.makeText(this, "New Project created successfully " + project.objectId, Toast.LENGTH_SHORT).show()

        //das heißt dass ich der Admin des Projekts bin
        status="true"

        //Gehe zu Activity in der Phasen angelegt werden und übergebe dabei zwei Variablen
        val intent = Intent(this@ChooseProject, setStages::class.java)
        println("Das ist meine objectId:${project.objectId}")
        intent.putExtra("projectID", project.objectId)
        intent.putExtra("deadline", project.deadLine)
        intent.putExtra("status", status)
        startActivity(intent)
    }
}









