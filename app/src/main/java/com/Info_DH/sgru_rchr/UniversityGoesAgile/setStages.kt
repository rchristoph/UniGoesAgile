package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_project.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_set_stages.*
import kotlinx.android.synthetic.main.activity_todo.*
import kotlinx.android.synthetic.main.content_main.*
import java.time.Month
import java.util.*

class setStages : AppCompatActivity() {

    var mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    val uid = user!!.uid
    lateinit var stageName: EditText
    lateinit var phasenBeginn: TextView
    lateinit var phasenEnde: TextView
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month : Int= c.get(Calendar.MONTH)
    //in Java January = Month Nr 0
    val day = c.get(Calendar.DAY_OF_MONTH)

    lateinit var listView: ListView

    lateinit var stageList: MutableList<Stage>
    lateinit var ref: DatabaseReference
    var stageId: String= ""
    var phasenSpeicher1: String = ""
    var phasenSpeicher2: String = ""
    var pruefVarStart: Boolean=false
    var pruefVarEnde: Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_stages)

        val projectID = intent.getStringExtra("projectID")
        val deadline = intent.getStringExtra("deadline")

        stageList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("Projects").child(projectID)

        stageName = findViewById(R.id.editStage)
        phasenBeginn = findViewById(R.id.showStart)
        phasenEnde = findViewById(R.id.showEnd)

        listView = findViewById(R.id.LIstview)


        addStage.setOnClickListener {
            saveStage()


            nextBtn1.setOnClickListener {
                startActivity(Intent(this, chooseNickname::class.java))

            }

        }

        //Datasnapshot
        var _stageListener = object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                println("Das ist mein SnapshotNeu:${p0.children}")
                println("Das sind meine Snapshots:${p0.child("Stages").value}")
                if (p0!!.exists()) {
                    stageList!!.clear()

                    for (h in p0.children) {
                        val stage = h.getValue(Stage::class.java)
                        stageList.add(stage!!)

                    }
                    val phasenSpeicher = p0.child(stageId).child("phasenEnde").getValue()
                    val adapter = StageAdapter(applicationContext, R.layout.stages, stageList)
                    listView.adapter = adapter
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        ref.child("Stages").child("Stage").addValueEventListener(_stageListener)
       var getStage = ref.child("Stages").child("Stage").child(stageId).toString()

//Calendar method
        startDate.setOnClickListener {
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth , mDay ->
                    showStart.setText("" + mDay + "/" + (mMonth+1) + "/" + mYear)
                    phasenSpeicher1 = ("" + (mDay + 1) + "/" + (mMonth + 1) + "/" + mYear)
                }, year, month, day)
            dpd.show()

            //Wenn es noch keine Phase gibt...
            if(pruefVarStart==false){
            // ist das minDate heute
                dpd.datePicker.minDate = CalendarHelper.getCurrentDateInMills()

                //Das maxDate vom Start muss vor bzw. am Ende der Deadline sein
                dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)
                pruefVarStart=true

            }else{
                //Startdatum darf erst am Folgetag vom letzten Tag der vorherigen Phase angelegt werden
                dpd.datePicker.minDate = CalendarHelper.convertDateToLong(phasenSpeicher2)

                //Datum darf nicht nach Ende der Deadline angelegt werden
                dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)

                pruefVarStart=true
            }


        }

        endDate1.setOnClickListener {
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->

                    showEnd.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear)
                    phasenSpeicher2 = ("" + (mDay + 1) + "/" + (mMonth + 1) + "/" + mYear)
                }, year, month, day)
                dpd.show()
                if(pruefVarEnde==false) {
                    //ermittle jetzigen Zeitpunkt
                    val jetzt = CalendarHelper.getCurrentDateInMills()

                    dpd.datePicker.minDate = CalendarHelper.convertDateToLong(phasenSpeicher1)

                    //Datum darf nicht nach Ende der Deadline angelegt werden
                    dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)
                    pruefVarEnde=true
                } else
                {
                    //Datum darf nicht nach Ende der Deadline angelegt werden
                dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)
                   //Datum darf erst am Folgetag vom letzten Tag der vorherigen Phase angelegt werden
                dpd.datePicker.minDate = CalendarHelper.convertDateToLong(phasenSpeicher1)
            }

        }


    }



    private fun saveStage() {
        val stageName = stageName.text.toString().trim()
        val start = phasenBeginn.text.toString()
        val ende = phasenEnde.text.toString()


        if(stageName.isEmpty() ){//&& start.isEmpty() && ende.isEmpty()){
           // stageName.error = "Please enter a stage and date"
            return
        }

        val newValue = ref.child("Stages").child("Stage")

        stageId = newValue.push().key.toString()

        val stage = Stage(stageName, start, ende, stageId)

        newValue.child(stageId).setValue(stage)

        Toast.makeText(applicationContext, "StageSaved!", Toast.LENGTH_LONG).show()

        showStart.text= ""
        showEnd.text = ""
        editStage.text.clear()
    }
}