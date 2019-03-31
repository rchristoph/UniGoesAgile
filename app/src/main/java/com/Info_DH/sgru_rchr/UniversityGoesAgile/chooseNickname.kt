package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_nickname.*

class chooseNickname : AppCompatActivity() {

    lateinit var mDatabase : DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    var newItem: String = ""
    var cities = arrayOf("Wähle eine Stadt", "Bangkok", "London", "Paris", "Sydney", "New York", "Istanbul", "Dubai", "Hanoi", "Hongkong", "Barcelona")
    var animals = arrayOf("Wähle ein Tier", "Bär", "Giraffe", "Eule", "Löwe", "Kranich", "Wiesel", "Okapi", "Wolf", "Robbe")
    var politic = arrayOf("Wähle PolitikerIn", "Merkel", "Obama", "May", "Macron", "Putin", "Orban", "Solberg", "Arden", "Abe")
    var history = arrayOf("Wähle eine Epoche", "Steinzeit", "Klassik", "Moderne", "Realismus", "Antike", "Aufklärung", "Renaissance")
    var strings = arrayOf("")
    var selectTheme: String = ""
    val user = mAuth.currentUser
    val uid = user!!.uid
    var projectTheme: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nickname)

        val projectID = intent.getStringExtra("projectID")
        println("")

        val nextBtn = findViewById<View>(R.id.nextBtn)

        mDatabase = FirebaseDatabase.getInstance().getReference("Names")

        //Hier brauche ich einen Datasnapshot
        var _themeListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    projectTheme = p0.child("Theme").getValue().toString()
                    println("PRJKT$projectTheme")
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        mDatabase.child("Projects").child(projectID).addValueEventListener(_themeListener)


        nextBtn.setOnClickListener(View.OnClickListener { view ->
            saveNickName(projectID)
        })

        //Prüfe ob es bereits ein Theme gibt
        if (!projectTheme.isEmpty()) {
            //Das bedeutet dass es noch kein Theme gibt weil der User das Projekt gerade neu angelegt hat
            val allThemes = arrayOf("", "Städte", "Tiere", "Politik", "Epochen")

            val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, allThemes)
            // Specify the layout to use when the list of choices appears
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner3.adapter = adapter2

            println("SelectThem:$selectTheme")
            selectedBtn.setOnClickListener {
                selectTheme = spinner3.selectedItem.toString()
                themeSelector(selectTheme)
            }

        } else {
            themeSelector(projectTheme)
        }
    }


    private fun saveNickName(projectID: String){
        var nickNameTxt = findViewById<View>(R.id.spinner1) as Spinner
        var themeTxt = findViewById<View>(R.id.spinner3) as Spinner
        val nickName = nickNameTxt.selectedItem.toString()
        val theme = themeTxt.selectedItem.toString()

        if (!nickName.isEmpty() && !theme.isEmpty()){
            val user = mAuth.currentUser
            val uid = user!!.uid
            mDatabase.child(uid).child("Name").child("Nickname").setValue(nickName)
            mDatabase.child("Projects").child(projectID).child("Theme").setValue(selectTheme)

            startActivity(Intent(this, MainActivity::class.java))

        }

    }

   private fun themeSelector(selectTheme: String) {
       if(selectTheme=="Städte") {
           strings = cities
       }else if(selectTheme=="Tiere"){
           strings = animals}else if(selectTheme=="Politik"){
           strings = politic} else{
           strings = history}
       println("Hier die Strings:$strings")
       val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, strings)
       // Specify the layout to use when the list of choices appears
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
       // Apply the adapter to the spinner
       spinner1.adapter = adapter
   }

}

