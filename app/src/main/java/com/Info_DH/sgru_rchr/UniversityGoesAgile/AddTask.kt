package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.text.InputType
import android.view.WindowManager
import android.widget.EditText
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*



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
    lateinit var _phasenList: MutableList<Phasen>
    var phasenarray:ArrayList<String>? = null
    lateinit var stageList: MutableList<Phasen>
    var test = ""

    lateinit var editText: EditText
    var onOk: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null
    lateinit var _adapter: StageAdapter
    lateinit var listView: ListView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(EXTRA_TITLE)
        val hint = arguments?.getString(EXTRA_HINT)


        _phasenList = mutableListOf<Phasen>()
        stageList = mutableListOf()




        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")


        var _taskListener = object : ValueEventListener {
            //Firebase delivers its data as a dataSnapshot
            override fun onDataChange(p0 : DataSnapshot) {

                if (p0!!.exists()) {


                    stageList!!.clear()

                    for (h in p0.children) {
                        val stage = h.getValue(Phasen::class.java)
                        stageList.add(stage!!)

                        println("stage::::::::$stage")

                    }
                    println("stagelist:::::: ${stageList}")

                    var aa = ArrayAdapter(activity, android.R.layout.simple_spinner_item, stageList)
                    aa.notifyDataSetChanged()
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinner2?.adapter = aa


                }


              /*  loadPhasenList(dataSnapshot)
                println("Datasnapshot loadphasenlist $dataSnapshot")*/

/*
        var stages = ArrayList<Stage>()
        for (snapshot in dataSnapshot.child("Stages").child("Stage").children) {
            var post = snapshot.getValue(Stage::class.java)
            println("Das ist der blöde Snapshot: $snapshot")
            println("Post: $post")
            stages.add(post!!)
            println("Post: $post")
        }
        println("Das sind die blöden Stages: ${stages}")
*/


            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
                //Log.w("ToDoActivity", "loadItem:onCancelled", databaseError.toException())
            }
        }


        var _projectListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(snapshot: DataSnapshot) {



                println("Der  Wert ist: ${snapshot.value}")
                projektIdent = snapshot.value.toString()
                println("Die Projektident vorm Funktionsstart ist: $projektIdent")
                println("Meine uid ist: $uid")

                _dbprojekt.child(projektIdent).child("Stages").addValueEventListener(_taskListener)


            }
        }

        _dbuser.child(uid).child("ProjektId").addValueEventListener(_projectListener)






        val view = activity!!.layoutInflater.inflate(R.layout.activity_add_task, null)

        editText = view.findViewById(R.id.editText)
        editText.hint = hint


   /*  val adapter = ArrayAdapter.createFromResource(activity,
            R.array.city_list, android.R.layout.simple_spinner_item)

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)*/
        // Apply the adapter to the spinner




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



/*    private fun loadPhasenList(dataSnapshot: DataSnapshot) {
        Log.d("ToDoActivity", "loadTaskList")

        val stages = dataSnapshot.children.iterator()
        println("Das ist das Task Objekt: $stages")

        //Check if current database contains any collection
        if (stages.hasNext()) {

             _phasenList!!.clear()


            val listIndex = stages.next()
            val itemsIterator = listIndex.children.iterator()
            var counter:Int = 0
            //check if the collection has any task or not
            while (itemsIterator.hasNext()) {


                //get current task
                val currentItem = itemsIterator.next()
                val stage = Phasen.create()

                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>

                //key will return the Firebase ID
                stage.phasenBeginn = map.get("phasenBeginn") as String
                stage.phasenEnde = map.get("phasenEnde") as String
                stage.stageId = map.get("stageId") as String
                stage.stageName = map.get("stageName") as String
                println("StageName::: ${stage}")



                phasenarray?.add(stage.stageName.toString())
                test = stage.stageName.toString()


                _phasenList?.add(stage)

            }

        }
          print("_phasenList = ${_phasenList[1].stageName.toString()}")


    }*/


}


