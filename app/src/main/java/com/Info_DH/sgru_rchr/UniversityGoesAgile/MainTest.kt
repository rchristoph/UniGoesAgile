package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*

class MainTest : FragmentActivity() {

    lateinit var _db: DatabaseReference
    lateinit var _dbuser: DatabaseReference
    lateinit var _dbprojekt: DatabaseReference
    val user = FirebaseAuth.getInstance().currentUser
    private val myRef: DatabaseReference? = null
    val uid = user!!.uid
    var projektIdent:String = ""




    val Context.myApp: MainTest
        get() = applicationContext as MainTest



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.canvas, Todofragment.newInstance(), "Test")
                .commit()
        }

/*        fab.setOnClickListener {view ->
            showFooter()
        }
        */

/*        btnAdd.setOnClickListener{ view ->
            addTask()
        }

*/


      /*
        // Get the text fragment instance
        val firstFragment = Todofragment()

        // Get the support fragment manager instance
        val manager = supportFragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.canvas,firstFragment)
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()

        */
        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _db = FirebaseDatabase.getInstance().getReference("tasks")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")



        var _projectListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                println("Der  Wert ist: ${snapshot.value}")
                projektIdent = snapshot.value.toString()
                println("Die Projektident vorm Funktionsstart ist: $projektIdent")
                if (snapshot.value == null){
                    startChoose()
                }

            }






        }







        //_db.orderByKey().addValueEventListener(_taskListener)
        _dbuser.child(uid).child("ProjektId").addValueEventListener(_projectListener)
        println("Die Projektident nr ist diese hier: $projektIdent")
       // println("das ist _dbproject: $_taskListener")



    }

    fun showFooter(){
        footer.visibility = View.VISIBLE
        fab.visibility = View.GONE
    }

    fun addTask(){

        //Declare and Initialise the Task
        val task = Task.create()

        //Set Task Description and isDone Status
        task.taskDesc = txtNewTaskDesc.text.toString()
        task.done = false
        task.author = user!!.uid
        task.edit = ""

        //Get the object id for the new task from the Firebase Database
        //Neue Tasks werden als Children von dem Projekt angelegt, dem der/die User_in zugewiesen ist.
        val newTask = _dbprojekt.child(projektIdent).child("tasks").child("task").push()

        task.objectId = newTask.key

        //Set the values for new task in the firebase using the footer form
        newTask.setValue(task)

        //Hide the footer and show the floating button
        footer.visibility = View.GONE
        fab.visibility = View.VISIBLE

        //Reset the new task description field for reuse.
        txtNewTaskDesc.setText("")

        Toast.makeText(this, "New Task added to the List successfully" + task.objectId, Toast.LENGTH_SHORT).show()
    }

    private fun startChoose(){
        println("Wir sind in der richtigen Funktion")
        startActivity(Intent(this, ChooseProject::class.java))
        Toast.makeText(this, "Choose Project", Toast.LENGTH_LONG).show()
    }


    interface TaskRowListener {

        fun onTaskChange(objectId: String, isDone: Boolean)
        fun onTaskDelete(objectId: String)
        fun onTaskEdit(objectId: String, taskDesc:String)

    }





}
