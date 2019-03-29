package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import android.widget.EditText
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class AddTask : android.support.v4.app.DialogFragment() {
    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_HINT = "hint"



        fun newInstance(title: String? = null, hint: String? = null): AddTask {
            val dialog = AddTask()
             val args = Bundle().apply {
               putString(EXTRA_TITLE, title)
                 putString(EXTRA_HINT, hint)
            }
            dialog.arguments = args
            return dialog
        }
    }

    lateinit var _dbuser: DatabaseReference
    lateinit var _dbprojekt: DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser
    var projektIdent:String = ""
    val uid = user!!.uid

    lateinit var editText: EditText
    var onOk: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(EXTRA_TITLE)
        val hint = arguments?.getString(EXTRA_HINT)


        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")

        var _projectListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                println("Der  Wert ist: ${snapshot.value}")
                projektIdent = snapshot.value.toString()
                println("Die Projektident vorm Funktionsstart ist: $projektIdent")
                println("Meine uid ist: $uid")

                //_dbprojekt.child(projektIdent).orderByKey().addValueEventListener(_taskListener)
            }
        }

        _dbuser.child(uid).child("ProjektId").addValueEventListener(_projectListener)




        val view = activity!!.layoutInflater.inflate(R.layout.activity_add_task, null)

        editText = view.findViewById(R.id.editText)
        editText.hint = hint


        val builder = AlertDialog.Builder(activity!!)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                addTask()
                onOk?.invoke()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                onCancel?.invoke()
            }
        val dialog = builder.create()

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        return dialog
    }

    fun addTask(){

        //Declare and Initialise the Task
        val task = Task.create()

        //Set Task Description and isDone Status
        task.taskDesc = editText.text.toString()
        task.done = false
        task.author = user!!.uid
        task.edit = ""
        task.assignee = "leer"

        //Get the object id for the new task from the Firebase Database
        //Neue Tasks werden als Children von dem Projekt angelegt, dem der/die User_in zugewiesen ist.
        val newTask = _dbprojekt.child(projektIdent).child("tasks").child("task").push()

        task.objectId = newTask.key

        //Set the values for new task in the firebase using the footer form
        newTask.setValue(task)

        Toast.makeText(context, "New Task added to the List successfully" + task.objectId, Toast.LENGTH_SHORT).show()
    }

}