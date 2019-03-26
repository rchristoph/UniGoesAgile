package com.Info_DH.sgru_rchr.UniversityGoesAgile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;

class Main : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.canvas, Todofragment.newInstance(), "Test")
                .commit()
        }




      /*
        // Get the text fragment instance
        val firstFragment = Todofragment()

        // Get the support fragment manager instance
        val manager = supportFragmentManager

        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()

        // Replace the fragment on container
        transaction.replace(R.id.canvas,firstFragment)
        transaction.addToBackStack(null)

        // Finishing the transition
        transaction.commit()

        */




    }
}
