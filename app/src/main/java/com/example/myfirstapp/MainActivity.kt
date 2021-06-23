package com.example.myfirstapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main_2.*

data class Modelclass(val image:Int, val name:String, val info:String )

class MainActivity : AppCompatActivity() {
    val arr= arrayListOf<Modelclass>()
    val names= arrayOf("Tracker","New Minimalism","Affirmations","Gratitude","Journaling","No Phone Challenge")
    val infos= arrayOf("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam"
        ,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam"
    ,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam"
    ,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam"
    ,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam"
    ,"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam")

    val images = arrayOf(R.drawable.ic_group_1,R.drawable.ic_group_2,R.drawable.ic_group_3,R.drawable.ic_group_4,R.drawable.ic_group_5,R.drawable.ic_group_6)
    lateinit var mAdapter:Myadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        //adding data into array
        for (i in names.indices){
            arr.add(Modelclass(images.get(i),names.get(i),infos.get(i)))
        }

        mAdapter = Myadapter(arr)
        mRecycler.layoutManager = GridLayoutManager(applicationContext,2)
        mRecycler.adapter = mAdapter

        logout.setOnClickListener {
            val sharedPref:SharedPreferences = getSharedPreferences(Myconstants.KEY,
                MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            startActivity(Intent(this,SplashScreen::class.java))
            finish()
        }


    }
}