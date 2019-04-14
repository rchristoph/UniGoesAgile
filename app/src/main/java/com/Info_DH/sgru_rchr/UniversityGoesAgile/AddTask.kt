package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.nav_header_drawer.*


class AddTask : android.support.v4.app.DialogFragment() {
    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_HINT = "hint"
        private const val EXTRA_LISTARRAY = "phasenlist"



        fun newInstance(title: String? = null, hint: String? = null, phasenlist: ArrayList<String>? = null): AddTask {
            val dialog = AddTask()
             val args = Bundle().apply {
               putString(EXTRA_TITLE, title)
                 putString(EXTRA_HINT, hint)
                 putStringArrayList(EXTRA_LISTARRAY, phasenlist)
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
    lateinit var _phasenList: MutableList<Phasen>
    var phasenarray:ArrayList<String>? = null
    lateinit var stageList: MutableList<Phasen>
    var test = ""

    lateinit var editText: EditText
    lateinit var meinspinner: Spinner
    var onOk: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null
    lateinit var _adapter: StageAdapter
    lateinit var listView: ListView
    lateinit var arrayList: ArrayList<String>
    var phase:Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(EXTRA_TITLE)
        val hint = arguments?.getString(EXTRA_HINT)
        val phasenlist = arguments?.getStringArrayList(EXTRA_LISTARRAY)

        print("Die phasenlist im add Task ist xxxxxxx = $phasenlist")


        _phasenList = mutableListOf<Phasen>()
        stageList = mutableListOf()
        arrayList = arrayListOf()


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

        meinspinner = view.findViewById(R.id.spinner2)

        println("die Phasen List in addTask yyyyyyyy = $phasenlist ")


        val aa =  ArrayAdapter(activity, R.layout.support_simple_spinner_dropdown_item, phasenlist)

        aa.notifyDataSetChanged()
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        meinspinner?.adapter = aa

        meinspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                println("onItemSelected position lalalala = $position id = $id")
                phase = id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }




      /*  val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, phasenlist)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner2?.adapter = adapter*/

       /* val aa = ArrayAdapter(activity, android.R.layout.simple_spinner_item, phasenlist)
        */


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
        task.phase = phase

        //Get the object id for the new task from the Firebase Database
        //Neue Tasks werden als Children von dem Projekt angelegt, dem der/die User_in zugewiesen ist.
        val newTask = _dbprojekt.child(projektIdent).child("tasks").child("task").push()

        task.objectId = newTask.key

        //Set the values for new task in the firebase using the footer form
        newTask.setValue(task)

        Toast.makeText(context, "New Task added to the List successfully" + task.objectId, Toast.LENGTH_SHORT).show()
    }

    /* fun getValues(view: View) {
         Toast.makeText(context, "Spinner 1 " + spinner2.selectedItem.toString() +
                 "\nSpinner 2 " + spinner2.selectedItem.toString(), Toast.LENGTH_LONG).show()
     }*/

}

