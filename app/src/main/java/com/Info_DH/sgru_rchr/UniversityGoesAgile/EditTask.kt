package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
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

        //Setze var database auf die richtige Stelle in der DB
        database = FirebaseDatabase.getInstance().getReference("Projects")
        val _dbuser = FirebaseDatabase.getInstance().getReference("Names")

        //Var aus der anderen Activity wird übergeben. So weiß ich auch hier welches Task gerade bearbeitet wird
        val parentId = intent.getStringExtra("obID")
        val tDesc = intent.getStringExtra("tDesc")

        //Wenn task-description bereits vorhanden hin, wird sie hier in das Textfeld gefüllt
        showTaskTitle.text = tDesc

        //Mit einem derartigen Eventlistener kann ich Daten gezielt aus der Datenbank laden
        val new: ValueEventListener = object : ValueEventListener {

            //Kopiert den Inhalt
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val task = dataSnapshot.child("tasks/task").child(parentId)
                    if (task.exists()) {
                        val task2 = task.child("edit").getValue()

                        val task3 = task2.toString()
                        if (task3 != "") {
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

        edit_btn.setOnClickListener(View.OnClickListener {
            editTxt(parentId)
        })

        //Projekt-ID wird im projectListener über Datasnapshot geladen. Wenn dies passiert ist wird der Tasklistener aufgerufen
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


    //Sorgt dafür dass das Text-Input Feld bei Klick auf Bearbeiten ggf. mit der vorhandenen Story befüllt ist
    private fun refreshView(task2: String){
        if (task2 != ""){
            newTextView.text= task2
            val storydesc = storyDescription
            storydesc.apply {
                setText(task2)
            }
            }
    }


    //Layout-Switch: Wechsel von Anzeigen zu Bearbeiten über den edit_btn
    private fun editTxt(parentId: String) {
        edit_btn.visibility = View.GONE
        newTextView.visibility = View.GONE
        save_button.visibility = View.VISIBLE
        storyDescription.visibility = View.VISIBLE
        textInputLayout6.visibility = View.VISIBLE

        save_button.setOnClickListener {
            view ->  saveTxt(parentId)
        }
    }

    //Layout-Switch von Bearbeitung zu Anzeigen
    private fun saveTxt(parentId: String) {
        edit_btn.visibility = View.VISIBLE
        newTextView.visibility = View.VISIBLE
        save_button.visibility = View.GONE
        storyDescription.visibility = View.GONE
        textInputLayout6.visibility = View.GONE

        //Setze den Textinput in Variablen und speichere diese in der DB
        val storyText = findViewById<View>(R.id.storyDescription) as EditText
        var story = storyText.text.toString()
        database = FirebaseDatabase.getInstance().getReference("Projects")
            if (!story.isEmpty()) {
                this.database.child(projektIdent).child("tasks/task").child(parentId).child("edit").setValue(story)
            }
    }
}




