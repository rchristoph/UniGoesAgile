package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.ShareActionProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.content_main.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Todo.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Todo.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Todo : Fragment(), PhasenListener {

    // TODO: Rename and change types of parameters
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
    private var listener: OnFragmentInteractionListener? = null
    lateinit var _adapter: TaskAdapter
    var phasenindikator:Long? = 0
    var zwei:Long = 2
  //  var phase: Long = 1
    lateinit var meinspinner: Spinner


    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"
        private val ARG_Phasen_NUMBER = "phasen_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int, phase: Long): Todo {
            val fragment = Todo()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
         //   args.putLong(ARG_Phasen_NUMBER, phase)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         phasenindikator = arguments?.getLong(ARG_Phasen_NUMBER)

            println("die Sektionsnummer ist: ${arguments.get("section_number")} ")




        _dbprojekt = FirebaseDatabase.getInstance().getReference("Projects")
        _dbuser = FirebaseDatabase.getInstance().getReference("Names")

        println("Die uid ist: $uid")



        var _taskListener = object : ValueEventListener {
            //Firebase delivers its data as a dataSnapshot
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println("Datasnapshot: ${dataSnapshot.child("tasks")}")
                loadTaskList(dataSnapshot.child("tasks"))
           //     toolbar.setTitle(dataSnapshot.child("projectName").value.toString())
            //    setSupportActionBar(toolbar)
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
                _dbprojekt.child(projektIdent).orderByKey().addValueEventListener(_taskListener)
            }
        }

        _taskList = mutableListOf<Task>()
        _adapter = TaskAdapter(context, _taskList!!)

        println("Der Adapter ist: $_adapter")


        //_db.orderByKey().addValueEventListener(_taskListener)
        _dbuser.child(uid).child("ProjektId").addValueEventListener(_projectListener)
        println("Die Projektident nr ist diese hier: $projektIdent")
        println("das ist _dbproject: $_taskListener")



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listviewTask!!.setAdapter(_adapter)
    }


     fun loadTaskList(dataSnapshot: DataSnapshot) {



        Log.d("ToDoActivity", "loadTaskList")

        val tasks = dataSnapshot.children.iterator()
        println("Das ist das Task Objekt: $tasks")

        //Check if current database contains any collection
        if (tasks.hasNext()) {

            _taskList?.clear()


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
                task.phase = map.get("phase") as Long

              //  println("testtesttest:::::: ${phasenindikator}")

                //task.edit = map.get("edit") as String?
                if(task.phase == phase) {
                    if (arguments.get("section_number") == 1) {
                        if (task.assignee == "leer" && task.done == false) {
                            _taskList!!.add(task)
                            println("_taskList: $_taskList")
                        }
                    } else if (arguments.get("section_number") == 2) {
                        if (task.assignee != "leer" && task.done == false) {
                            _taskList!!.add(task)
                        }
                    } else if (arguments.get("section_number") == 3) {
                        if (task.done == true) {
                            _taskList!!.add(task)
                        }
                    }
                }


            }
            println("_taskList = $_taskList")

        }
        //alert adapter that has changed
        _adapter.notifyDataSetChanged()
    }



    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }




}
