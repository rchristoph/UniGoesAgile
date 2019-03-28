/*
package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*
import com.google.firebase.database.*
import android.support.v7.widget.ShareActionProvider
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.task_rows.*


class ToDoActivity : AppCompatActivity(), TaskRowListener {

    lateinit var _dbuser: DatabaseReference
    lateinit var _dbprojekt: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val myRef: DatabaseReference? = null
    val uid = user!!.uid
    var projektIdent:String = ""
    private var shareActionProvider: ShareActionProvider? = null
    var _taskList: MutableList<Task>? = null
    var nameIdent : String = ""
    var nickWert2: String  = ""


    lateinit var _adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")

        println("Die uid ist: $uid")



        var _taskListener = object : ValueEventListener {
            //Firebase delivers its data as a dataSnapshot
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("Datasnapshot: ${dataSnapshot.child("tasks")}")
                loadTaskList(dataSnapshot.child("tasks"))
                toolbar.setTitle(dataSnapshot.child("projectName").value.toString())
                setSupportActionBar(toolbar)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
                Log.w("ToDoActivity", "loadItem:onCancelled", databaseError.toException())
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
                if (snapshot.value == null){
                    startChoose()
                }
                _dbprojekt.child(projektIdent).orderByKey().addValueEventListener(_taskListener)
            }
        }

        _taskList = mutableListOf<Task>()
        _adapter = TaskAdapter(this, _taskList!!)

        listviewTask!!.setAdapter(_adapter)


        fab.setOnClickListener {view ->
            showFooter()
        }

        btnAdd.setOnClickListener{ view ->
            addTask()
        }

        //_db.orderByKey().addValueEventListener(_taskListener)
        _dbuser.child(uid).child("ProjektId").addValueEventListener(_projectListener)
        println("Die Projektident nr ist diese hier: $projektIdent")
        println("das ist _dbproject: $_taskListener")

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        val shareItem = menu!!.findItem(R.id.menu_item_share)

        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider

        setShareIntent()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.signOut)
        {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show()
        }
        else if (item!!.itemId == R.id.signoutofproject){
            startActivity(Intent(this, ChooseProject::class.java))

        }
        else if (item!!.itemId == R.id.menu_item_share){
        }
        return super.onOptionsItemSelected(item)
    }

    //Funktion für das TEilen der Prokekt ID
    private fun setShareIntent() {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Projekt ID: ")
        shareIntent.putExtra(Intent.EXTRA_TEXT, projektIdent)
        shareActionProvider?.setShareIntent(shareIntent)
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
        task.assignee = "leer"

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
    private fun loadTaskList(dataSnapshot: DataSnapshot) {
        Log.d("ToDoActivity", "loadTaskList")

        val tasks = dataSnapshot.children.iterator()
        println("Das ist das Task Objekt: $tasks")

        //Check if current database contains any collection
        if (tasks.hasNext()) {

            _taskList!!.clear()


            val listIndex = tasks.next()
            val itemsIterator = listIndex.children.iterator()

            //check if the collection has any task or not
            while (itemsIterator.hasNext()) {

                //get current task
                val currentItem = itemsIterator.next()
                val task = Task.create()

                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>

                //key will return the Firebase ID
                task.objectId = currentItem.key
                task.done = map.get("done") as Boolean?
                task.taskDesc = map.get("taskDesc") as String?
                task.assignee = map.get("assignee") as String?

                //task.edit = map.get("edit") as String?
                _taskList!!.add(task)
            }
        }
        //alert adapter that has changed
        _adapter.notifyDataSetChanged()
    }

    private fun startChoose(){
        println("Wir sind in der richtigen Funktion")
        startActivity(Intent(this, ChooseProject::class.java))
        Toast.makeText(this, "Choose Project", Toast.LENGTH_LONG).show()
    }

    override fun onTaskChange(objectId: String, isDone: Boolean) {
        //val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        val task = _dbprojekt.child(projektIdent).child("tasks").child("task").child(objectId)
        task.child("done").setValue(isDone)
    }

    override fun onTaskDelete(objectId: String) {
        // val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        val task = _dbprojekt.child(projektIdent).child("tasks").child("task").child(objectId)
        task.removeValue()
        println("Das ist ist task: $task")
    }
    override fun onTaskEdit(objectId: String, taskDesc: String) {
        //hier nehme ich die Task-ID, von der aus ich in der naechsten Activity direkt auf die Childnodes zugreifen kann
        val intent = Intent(this@ToDoActivity, EditTask::class.java)
        intent.putExtra("obID", objectId)
        intent.putExtra("tDesc", taskDesc)
        startActivity(intent)

    }

    override fun onTaskAssign(objectId: String) {
        _dbuser = FirebaseDatabase.getInstance().getReference("Names").child(uid)
        if (nickWert2.isEmpty()) {

            var _nameListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    println("Der  Wert ist: ${snapshot.value}")
                    nameIdent = snapshot.value.toString()
                    println("Die Name ident vorm Funktionsstart ist: $nameIdent")
                    println("Meine uid ist: $uid")
                    var nickWert = snapshot.child("nickName")
                    var nickWert2 = nickWert.getValue().toString()
                    println("NICKWERT:$nickWert")
                    zsFassung(nickWert2, objectId)

                }

            }

            _dbuser.child(nameIdent).addValueEventListener(_nameListener)
        } else {
            zsFassung(nickWert2, objectId)
        }
    }

    private fun zsFassung (nickWert:String, objectId: String) {

        var stelle = _dbprojekt.child(projektIdent).child("tasks/task").child(objectId).child("assignee")
        stelle.setValue(nickWert)
    }


}
*/
