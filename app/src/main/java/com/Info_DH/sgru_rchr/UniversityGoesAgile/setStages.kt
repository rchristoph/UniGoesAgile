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

    //User-Vars
    var mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser
    //Stage-Vars
    lateinit var stageName: EditText
    lateinit var phasenBeginn: TextView
    lateinit var phasenEnde: TextView

    //Calendar(-function) Vars
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month : Int= c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    var phasenSpeicher1: String = ""
    var phasenSpeicher2: String = ""
    var pruefVarStart: Boolean=false
    var pruefVarEnde: Boolean=false

    //Stage-List-View
    lateinit var listView: ListView
    lateinit var stageList: MutableList<Stage>
    lateinit var ref: DatabaseReference
    var stageId: String= ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_stages)

        val projectID = intent.getStringExtra("projectID")
        val deadline = intent.getStringExtra("deadline")
        val status = intent.getStringExtra("status")

        stageList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("Projects").child(projectID)

        //Get names/dates filled out by the user
        stageName = findViewById(R.id.editStage)
        phasenBeginn = findViewById(R.id.showStart)
        phasenEnde = findViewById(R.id.showEnd)
        listView = findViewById(R.id.LIstview)

        //Add stage



        addStage.setOnClickListener {
            saveStage()

        //Or move to next activity
        nextBtn1.setOnClickListener {
            val intent = Intent(this, chooseNickname::class.java)
            intent.putExtra("projectID", projectID)
            intent.putExtra("status", status)
            startActivity(intent)
            }
        }

        //Datasnapshot to show saved stages in List-View by the help of adapter
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
                    val adapter = StageAdapter(applicationContext, R.layout.stages, stageList)
                    listView.adapter = adapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        ref.child("Stages").child("Stage").addValueEventListener(_stageListener)

        //Calendar method
        startDate.setOnClickListener {
                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth , mDay ->
                    //Month must be +1 because in java January has nr 0
                    showStart.setText("" + mDay + "/" + (mMonth+1) + "/" + mYear)

                    //phasenSpeicher1+2: Store the date of the beginning and end of stages. With their help I can avoid overlapping stages
                    phasenSpeicher1 = ("" + (mDay + 1) + "/" + (mMonth + 1) + "/" + mYear)
                }, year, month, day)
            dpd.show()

            //If it's the first stage:
            if(pruefVarStart==false){

                // minDate = today
                dpd.datePicker.minDate = CalendarHelper.getCurrentDateInMills()

                //maxDate must be before the end of the deadline
                dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)
                pruefVarStart=true

            }else{

                //If there's already a stage, minDate must be set 1 day after last stage ended
                dpd.datePicker.minDate = CalendarHelper.convertDateToLong(phasenSpeicher2)

                //Stage-Date must be before deadline is over
                dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)

                //By changing the pruefVarStart I know next time I click on the start-date, that there is already a stage
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
                    //ermittle jetzigen Zeitpunkt als long
                    dpd.datePicker.minDate = CalendarHelper.convertDateToLong(phasenSpeicher1)
                    dpd.datePicker.maxDate = CalendarHelper.convertDateToLong(deadline)
                    pruefVarEnde=true
                } else
                {
                    //Date must be before the end of the deadline
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

        if(stageName.isEmpty() && start.isEmpty() && ende.isEmpty()){
            Toast.makeText(this, "Fill out name field and choose start and end-date", Toast.LENGTH_LONG).show()
        }else {

            //Save stages in DB
            val newValue = ref.child("Stages").child("Stage")
            stageId = newValue.push().key.toString()
            val stage = Stage(stageName, start, ende, stageId)
            newValue.child(stageId).setValue(stage)
            Toast.makeText(applicationContext, "StageSaved!", Toast.LENGTH_LONG).show()

            //Clear Input-Fields
            showStart.text = ""
            showEnd.text = ""
            editStage.text.clear()
        }
    }
}