package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_navigationdrawer.*
import kotlinx.android.synthetic.main.app_bar_navigationdrawer.*
import android.content.Intent
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*
import com.google.firebase.database.*
import android.support.v7.widget.ShareActionProvider
import android.widget.ImageView
import kotlinx.android.synthetic.main.nav_header_navigationdrawer.*
import java.net.URL
import android.graphics.BitmapFactory
import android.graphics.Bitmap




class ToDoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, TaskRowListener {

    lateinit var _db: DatabaseReference
    lateinit var _dbuser: DatabaseReference
    lateinit var _dbprojekt: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val myRef: DatabaseReference? = null
    val uid = user!!.uid
    var projektIdent:String = ""
    private var shareActionProvider: ShareActionProvider? = null
    var _taskList: MutableList<Task>? = null

    lateinit var _adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigationdrawer)

        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _db = FirebaseDatabase.getInstance().getReference("tasks")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")
        val imageView = findViewById<ImageView>(R.id.Profilbild)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


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
                println("Der  Wert ist: ${snapshot.child("ProjektId").value}")
                projektIdent = snapshot.child("ProjektId").value.toString()
                username.text = "${snapshot.child("Name").value.toString()}"
                nickname.text = "${snapshot.child("nickName").value.toString()} (Nickname)"


                println("Die Projektident vorm Funktionsstart ist: $projektIdent")
                if (snapshot.child("ProjektId").value == null){
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
        _dbuser.child(uid).addValueEventListener(_projectListener)
        println("Die Projektident nr ist diese hier: $projektIdent")
        println("das ist _dbproject: $_taskListener")


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)







    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuInflater.inflate(R.menu.navigationdrawer, menu)

        val shareItem = menu!!.findItem(R.id.menu_item_share)

        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider

        // Inflate the menu; this adds items to the action bar if it is present.

        setShareIntent()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item!!.itemId == R.id.signOut)
        {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show()
            return super.onOptionsItemSelected(item)
        }
        else if (item!!.itemId == R.id.signoutofproject){
            startActivity(Intent(this, ChooseProject::class.java))
            return super.onOptionsItemSelected(item)

        }
        else if (item!!.itemId == R.id.menu_item_share){
            return super.onOptionsItemSelected(item)
        }

        when (item.itemId) {
            R.id.action_settings2 -> return true
            else -> return super.onOptionsItemSelected(item)
        }


    }


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
        val intent = Intent(this, EditTask::class.java)
        intent.putExtra("obID", objectId)
        intent.putExtra("tDesc", taskDesc)
        startActivity(intent)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
