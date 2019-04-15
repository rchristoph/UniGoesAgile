package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_choose_nickname.*
import kotlinx.android.synthetic.main.activity_choose_project.*

class chooseNickname : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    lateinit var realDatabase: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    var cities = arrayOf(
        "Wähle eine Stadt",
        "Bangkok",
        "London",
        "Paris",
        "Sydney",
        "New York",
        "Istanbul",
        "Dubai",
        "Hanoi",
        "Hongkong",
        "Barcelona"
    )
    var animals =
        arrayOf("Wähle ein Tier", "Bär", "Giraffe", "Eule", "Löwe", "Kranich", "Wiesel", "Okapi", "Wolf", "Robbe")
    var politic =
        arrayOf("Wähle PolitikerIn", "Merkel", "Obama", "May", "Macron", "Putin", "Orban", "Solberg", "Arden", "Abe")
    var history = arrayOf(
        "Wähle eine Epoche",
        "Steinzeit",
        "Klassik",
        "Moderne",
        "Realismus",
        "Antike",
        "Aufklärung",
        "Renaissance"
    )
    var strings = arrayOf("")
    var selectTheme: String = ""
    val user = mAuth.currentUser
    val uid = user!!.uid
    var projectTheme: String = ""
    val _dbuser = FirebaseDatabase.getInstance().getReference("Names")
    var proID: String = ""
    var status: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nickname)


        val nextBtn = findViewById<View>(R.id.nextBtn)

        mDatabase = FirebaseDatabase.getInstance().getReference("Names")
        realDatabase = FirebaseDatabase.getInstance().getReference("Projects")
        status = intent.getStringExtra("status")


        //Hier brauche ich einen Datasnapshot
        var _themeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("bin jetzt hier!!!!, das ist mein Snapshot:$snapshot")
                var newVal123 = snapshot.child(proID).child("theme").value.toString()
                println("1636: $newVal123")
                if (status == "false") {
                    println("STATUS ist falsch")
                    themeSelector(newVal123)

                } else {
                    textView10.visibility = View.VISIBLE
                    selectedBtn.visibility = View.VISIBLE
                    spinner3.visibility = View.VISIBLE
                    themeAdapt()
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

        var _projectListener = object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                proID = snapshot.child("ProjektId").value.toString()
                println("das ist meine ProID: $proID")

                realDatabase.addValueEventListener(_themeListener)
            }


        }

        _dbuser.child(uid).child(proID).addValueEventListener(_projectListener)



        nextBtn.setOnClickListener(View.OnClickListener { view ->
            saveNickName(proID)
        })


    }


    private fun saveNickName(projectID: String) {
        if (status == "true") {
            var nickNameTxt = findViewById<View>(R.id.spinner1) as Spinner
            var themeTxt = findViewById<View>(R.id.spinner3) as Spinner
            val nickName = nickNameTxt.selectedItem.toString()
            val theme = themeTxt.selectedItem.toString()

            if (!nickName.isEmpty() && !theme.isEmpty()) {
                val user = mAuth.currentUser
                val uid = user!!.uid
                mDatabase.child(uid).child("nickname").setValue(nickName)
                realDatabase.child(projectID).child("theme").setValue(selectTheme)
                val newValue = realDatabase.child(projectID).child("members")
                val nName = Members(nickName)
                newValue.child(uid).setValue(nName)

                newValue.child(nickName)

                startActivity(Intent(this, MainActivity::class.java))

            }
        } else {
            var nickNameTxt = findViewById<View>(R.id.spinner1) as Spinner
            val nickName = nickNameTxt.selectedItem.toString()

            if (!nickName.isEmpty()) {
                val user = mAuth.currentUser
                val uid = user!!.uid
                mDatabase.child(uid).child("nickname").setValue(nickName)
                val newValue = realDatabase.child(projectID).child("members")
                val nName = Members(nickName)
                newValue.child(uid).setValue(nName)

                newValue.child(nickName)

                startActivity(Intent(this, MainActivity::class.java))
            }

        }
    }

        //Prüfe ob es bereits ein Theme gibt
        private fun themeAdapt() {
            //Das bedeutet dass es noch kein Theme gibt weil der User das Projekt gerade neu angelegt hat
            val allThemes = arrayOf("", "Städte", "Tiere", "Politik", "Epochen")

            val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, allThemes)
            // Specify the layout to use when the list of choices appears
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner3.adapter = adapter2


            selectedBtn.setOnClickListener {
                selectTheme = spinner3.selectedItem.toString()
                println("SelectThem:$selectTheme")

                themeSelector(selectTheme)
            }
        }

        private fun themeSelector(selectTheme: String) {
            if (selectTheme == "Städte") {
                strings = cities
            } else if (selectTheme == "Tiere") {
                strings = animals
            } else if (selectTheme == "Politik") {
                strings = politic
            } else {
                strings = history
            }
            println("Hier die Strings:$strings")

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, strings)
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }


}
