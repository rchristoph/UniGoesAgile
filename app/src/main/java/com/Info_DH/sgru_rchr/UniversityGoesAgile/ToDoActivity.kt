package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.os.Bundle
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


class ToDoActivity : AppCompatActivity(), TaskRowListener {

    lateinit var _db: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser


    var _taskList: MutableList<Task>? = null

    lateinit var _adapter: TaskAdapter


    var _taskListener: ValueEventListener = object : ValueEventListener {
        //Firebase delivers its data as a dataSnapshot
        //sobald sich die Daten der Liste aendern muss die Liste neu geladen werden
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadTaskList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("ToDoActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)
        setSupportActionBar(toolbar)

        _db = FirebaseDatabase.getInstance().getReference("tasks")
        _taskList = mutableListOf<Task>()


        _adapter = TaskAdapter(this, _taskList!!)
        listviewTask!!.setAdapter(_adapter)



        fab.setOnClickListener {view ->
            showFooter()
        }

        btnAdd.setOnClickListener{ view ->
            addTask()
        }

        _db.orderByKey().addValueEventListener(_taskListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.signOut)
        {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
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
        val newTask = _db.child(Statics.FIREBASE_TASK).push()
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
        Log.d("TodoActivity", "TodoActHier ein Snapshot:$tasks")

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
                //task.edit = map.get("edit") as String?
                _taskList!!.add(task)
            }
        }

        //alert adapter that has changed
        _adapter.notifyDataSetChanged()

    }
    override fun onTaskChange(objectId: String, isDone: Boolean) {
        val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        task.child("done").setValue(isDone)
    }

    override fun onTaskDelete(objectId: String) {
        val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        task.removeValue()
    }
    override fun onTaskEdit(objectId: String, taskDesc: String) {
        //hier nehme ich die Task-ID, von der aus ich in der naechsten Activity direkt auf die Childnodes zugreifen kann
        val intent = Intent(this@ToDoActivity, EditTask::class.java)
        intent.putExtra("obID", objectId)
        intent.putExtra("tDesc", taskDesc)
        startActivity(intent)
    }
}
