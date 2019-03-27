package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_project.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_set_stages.*
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class setStages : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val uid = user!!.uid
    lateinit var editStageName: EditText
    lateinit var startDatum: TextView
    lateinit var endDatum: TextView
    lateinit var addBtn: Button
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    lateinit var listView: ListView

    lateinit var stageList: MutableList<Stage>
    lateinit var ref: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_stages)

        val projectID = intent.getStringExtra("projectID")

        stageList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("Projects").child(projectID)

        editStageName = findViewById(R.id.editStage)
        startDatum = findViewById(R.id.startDate)
        endDatum = findViewById(R.id.endDate)

        listView = findViewById(R.id.LIstview)


        addStage.setOnClickListener {
            saveStage(projectID)


        }

        startDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->

                showStart.setText("" + mDay + "/" + mMonth + "/" + mYear)
            }, year, month, day)

            dpd.show()
        }

        endDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->

                showEnd.setText("" + mDay + "/" + mMonth + "/" + mYear)
            }, year, month, day)

            dpd.show()

        }

        var _stageListener = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    for (h in p0.children) {
                        val stage = h.getValue(Stage::class.java)
                        stageList.add(stage!!)
                    }
                    val adapter = StageAdapter(applicationContext, R.layout.stages, stageList)
                    listView.adapter = adapter
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        ref.addValueEventListener(_stageListener)

    }



    private fun saveStage(projectID:String) {
        val stageName = editStageName.text.toString().trim()
        val start = showStart.text.toString()
        val ende = showEnd.text.toString()



        if(stageName.isEmpty() && start.isEmpty() && ende.isEmpty()){
            editStageName.error = "Please enter a stage and date"
            return
        }

        val stageId = ref.push().key.toString()

        val stage = Stage(stageId, stageName, start, ende)

        ref.child(stageId).setValue(stage)

        Toast.makeText(applicationContext, "StageSaved!", Toast.LENGTH_LONG).show()
    }


}