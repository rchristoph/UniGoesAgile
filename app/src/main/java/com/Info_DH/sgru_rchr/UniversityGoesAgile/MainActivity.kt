package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.support.v4.widget.DrawerLayout

import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.webkit.WebViewFragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_drawer.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.content_drawer.*
import kotlinx.android.synthetic.main.nav_phasenswitcher.*


class MainActivity : AppCompatActivity(), TaskRowListener, NavigationView.OnNavigationItemSelectedListener  {


    lateinit var _dbuser: DatabaseReference
    lateinit var _dbprojekt: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    private val myRef: DatabaseReference? = null
    val uid = user!!.uid
    var projektIdent:String = ""
    private var shareActionProvider: ShareActionProvider? = null
    var _taskList: MutableList<Task>? = null
    var _phasenList: MutableList<Task>? = null
    var nameIdent : String = ""
    var nickWert2: String  = ""
    lateinit var stageList: MutableList<Phasen>
    lateinit var arrayList: ArrayList<String>
    var phase:Long = 0
    val obj = Todo()

    lateinit var _adapter: TaskAdapter


    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stageList = mutableListOf()
        arrayList = arrayListOf()



        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this@MainActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this@MainActivity)


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        var _spinnerlistener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // loadPhasenList(dataSnapshot)
                if (dataSnapshot.child("Stages")!!.exists()) {

                    stageList!!.clear()
                    arrayList!!.clear()

                    for (h in dataSnapshot.child("Stages").child("Stage").children) {
                        val stage = h.getValue(Phasen::class.java)
                        println(h.getValue().toString())
                        stageList.add(stage!!)
                        //    arrayList.add(dataSnapshot.child("Stages").child("Stage").child("-Lb5FqD2R9NezzOIiZwU").child("stageName").value.toString())
                        arrayList.add(stage!!.stageName)
                        println("Stage.stageName = ${stage.stageName}")


                        println("stage::::::::$stage")


                    }
                    println("stagelist:::::: ${stageList}")
                    println("ArrayList:::: $arrayList")

                    var aa = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, arrayList)
                    aa.notifyDataSetChanged()
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner3?.adapter = aa




                }

            }

        }



        //get all Tasks from Database
        var _taskListener = object : ValueEventListener {

            //Firebase delivers its data as a dataSnapshot

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               /* toolbar.setTitle("${dataSnapshot.child("projectName").value.toString()} | Phase: ${phase+1}/${arrayList.size}")
                setSupportActionBar(toolbar)*/


                spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        println("onItemSelected position Main = $position id = $id")
                        phase = id
                        println("------phase-------$phase")
                        //  loadTaskList()

                        obj.phase = phase

                        toolbar.setTitle("${dataSnapshot.child("projectName").value.toString()} | Phase: ${phase+1}/${arrayList.size}")
                        setSupportActionBar(toolbar)

                        val toggle = ActionBarDrawerToggle(
                            this@MainActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
                        )
                        drawer_layout.addDrawerListener(toggle)
                        toggle.syncState()
                        nav_view.setNavigationItemSelectedListener(this@MainActivity)
                        projektname.text = dataSnapshot.child("projectName").value.toString()

                        //Get Name of project
                        projektname.text = dataSnapshot.child("projectName").value.toString()



                        container.adapter.notifyDataSetChanged()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }

                //Hier wird die RecycleView mit den Phasen gefüllt:

                // Creates a vertical Layout Manager
                rv_phasenmenu.layoutManager = LinearLayoutManager(this@MainActivity)

                // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

                // Access the RecyclerView Adapter and load the data into it
                rv_phasenmenu.adapter = PhasenMenuAdapter(arrayList, this@MainActivity,{ Items : Int -> partItemClicked(Items, dataSnapshot) })



            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
                //Log.w("ToDoActivity", "loadItem:onCancelled", databaseError.toException())
            }
        }
        //PROJECTListener will be called first, then it will call the Tasklistener to get all tasks of a project
        var _projectListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            //Snapshot to get data of the right project
            override fun onDataChange(snapshot: DataSnapshot) {
                println("Der  Wert ist: ${snapshot.value}")
                projektIdent = snapshot.child("ProjektId").value.toString()
                println("Die Projektident vorm Funktionsstart ist: $projektIdent")
                println("Meine uid ist: $uid")
                username.text = "${snapshot.child("name").value.toString()}"
                nickname.text = "${snapshot.child("nickname").value.toString()} (Nickname)"
                nickWert2 = snapshot.child("nickname").value.toString()
                println("           Nickwert = $nickWert2")


                if (snapshot.value == null){
                    startChoose()
                }
                _dbprojekt.child(projektIdent).orderByKey().addValueEventListener(_taskListener)
                _dbprojekt.child(projektIdent).orderByKey().addListenerForSingleValueEvent(_spinnerlistener)
            }
        }

        _dbuser.child(uid).addValueEventListener(_projectListener)


        fab!!.setOnClickListener { view ->

            val dialog = AddTask.newInstance(title= "Neue Aufgabe hinzufügen", hint = "Name der Aufgabe", phasenlist = arrayList)
            dialog.show(supportFragmentManager, "editDescription")
        }


        // enabling Toolbar bar app icon and behaving it as toggle button
/*
        var fragment = supportFragmentManager.getFragment() as WebViewFragment
        fragment.loadTaskList(DataSnapshot, phase)*/
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)

        } else {
            super.onBackPressed()
        }
    }

    private fun partItemClicked(Items : Int, dataSnapshot: DataSnapshot) {
        Toast.makeText(this, "Clicked: ${Items}", Toast.LENGTH_LONG).show()
         phase = Items.toLong()

        container.adapter.notifyDataSetChanged()


        toolbar.setTitle("${dataSnapshot.child("projectName").value.toString()} | Phase: ${phase+1}/${arrayList.size}")
        setSupportActionBar(toolbar)

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            val toggle = ActionBarDrawerToggle(
                this@MainActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer_layout.addDrawerListener(toggle)
            toggle.syncState()
            nav_view.setNavigationItemSelectedListener(this@MainActivity)

        }



    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            println("Die Phasen Nummer ist folgendes 1#######1 ${phase}")
            return Todo.newInstance(position + 1, phase)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3

        }

        override fun getItemPosition(`object`: Any?): Int {
            return POSITION_NONE
        }
    }

    //If user clicks on Checkbox
    override fun onTaskChange(objectId: String, isDone: Boolean) {
        val task = _dbprojekt.child(projektIdent).child("tasks").child("task").child(objectId)
        task.child("done").setValue(isDone)
    }

    //If User deletes Task
    override fun onTaskDelete(objectId: String) {
        // val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        val task = _dbprojekt.child(projektIdent).child("tasks").child("task").child(objectId)
        task.removeValue()
        println("Das ist ist task: $task")
    }

    //To write Userstory - start new activity
    override fun onTaskEdit(objectId: String, taskDesc: String) {
        //hier nehme ich die Task-ID, von der aus ich in der naechsten Activity direkt auf die Childnodes zugreifen kann
        val intent = Intent(this, EditTask::class.java)
        intent.putExtra("obID", objectId)
        intent.putExtra("tDesc", taskDesc)
        startActivity(intent)
    }

    //If user assigns a task to himself
    override fun onTaskAssign(objectId: String) {
/*        _dbuser = FirebaseDatabase.getInstance().getReference("Names").child(uid)
        if (nickWert2.isEmpty()) {

            var _nameListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                //Get another Datasnapshot of the right Nickname
                override fun onDataChange(snapshot: DataSnapshot) {
                    println("Der  Wert ist: ${snapshot.value}")
                    nameIdent = snapshot.value.toString()
                    println("Die Name ident vorm Funktionsstart ist: $nameIdent")
                    println("Meine uid ist: $uid")
                    var nickWert = snapshot.child("Name").child("Nickname")
                    var nickWert2 = nickWert.getValue().toString()
                    println("NICKWERT:$nickWert")
                    zsFassung(nickWert2, objectId)
                }
            }
            //Call namelistener
            _dbuser.child(nameIdent).addValueEventListener(_nameListener)
        } else {
            zsFassung(nickWert2, objectId)
        }*/

        zsFassung(nickWert2, objectId)
    }

    override fun onTaskAssigndelete(objectId: String){

        var stelle = _dbprojekt.child(projektIdent).child("tasks/task").child(objectId).child("assignee")
        stelle.setValue("leer")


    }



    //Write the assigned user to the DB
    private fun zsFassung (nickWert:String, objectId: String) {

        var stelle = _dbprojekt.child(projektIdent).child("tasks/task").child(objectId).child("assignee")
        stelle.setValue(nickWert)

    }

    //Menu to sign out, share or change project
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer, menu)
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
            return super.onOptionsItemSelected(item)

        }
/*        else if (item!!.itemId == R.id.action_settings) {
            return true
        }*/

        else if (item!!.itemId == R.id.action_settings2 ) {
              return true
        }
            return super.onOptionsItemSelected(item)

    }

    //Funktion für das Teilen der Prokekt ID
    private fun setShareIntent() {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Projekt ID: ")
        shareIntent.putExtra(Intent.EXTRA_TEXT, projektIdent)
        shareActionProvider?.setShareIntent(shareIntent)
    }



    private fun startChoose(){
        println("Wir sind in der richtigen Funktion")
        startActivity(Intent(this, ChooseProject::class.java))
        Toast.makeText(this, "Choose Project", Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {
                Toast.makeText(this, "KLicken funktioniert", Toast.LENGTH_LONG).show()

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





/*    private fun loadPhasenList(dataSnapshot: DataSnapshot) {
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
*//*                if(arguments.get("section_number")== 1) {
                    if (task.assignee == "leer"&&task.done == false) {
                        _taskList!!.add(task)
                    }
                }
                else if(arguments.get("section_number")== 2) {
                    if (task.assignee != "leer"&& task.done == false) {
                        _taskList!!.add(task)
                    }
                }
                else if(arguments.get("section_number")== 3) {
                    if (task.done == true) {
                        _taskList!!.add(task)
                    }*//*
                }
            }
        }
        //alert adapter that has changed
    //    _adapter.notifyDataSetChanged()
    //}*/


}


