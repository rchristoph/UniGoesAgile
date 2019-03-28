package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity(), TaskRowListener {


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


    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_tabs, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): Todo {
                val fragment = Todo()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
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
    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        val shareItem = menu!!.findItem(R.id.menu_item_share)

        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider

        setShareIntent()
        return super.onCreateOptionsMenu(menu)
    }*/

   /* override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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
    }*/

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




}
