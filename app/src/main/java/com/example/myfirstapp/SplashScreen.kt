package com.example.myfirstapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Myconstants.Companion.KEY
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.datastore.Mydataclass
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.log

class SplashScreen : AppCompatActivity() {

    lateinit var mSharedprefrences:SharedPreferences
    val tag = "//"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mSharedprefrences=getSharedPreferences(KEY, MODE_PRIVATE)

        val mDataclass = Mydataclass(this)
        lifecycleScope.async {
            mDataclass.saveValue("abc")
            mDataclass.dataStore.data
        }

        mDataclass.mValue.asLiveData().observe(this, Observer {
            Log.e(TAG, "onCreate: ")
        })


//        mSharedprefrences.getString("a", null)?.also {
//            Log.e("//", "onCreate:1 $it" )
//        }.apply {
//            Log.e("//", "onCreate: " )
//        }
//
//        val obj:Abc = Abc("A")
//        Log.e(TAG, "onCreate: "+obj.name )

//        val let = Abc().let {
//            it.mage = 10
//        }
//        Log.e("//", "onCreate: "+let )

//
//        val abc:Abc? = Abc()
//
//        val mrun = abc?.run {
//            age = 20
//            name = "ss"
//            return@run age
//        }

//
//        val apply = Abc().apply {
//            name = "shubham"
//            age = 10
//        }
//        Log.e(tag, "onCreate: "+mrun +"${abc?.name}")




        login.setOnClickListener{
            if (mSharedprefrences.getBoolean("isopen",false)){
                startActivity(Intent(this,Lastscreen::class.java))
            }else{
                mSharedprefrences.edit().putBoolean("isopen",true).apply()
                startActivity(Intent(this,Demoactivity::class.java))
            }
        }
    }
}
class Abc (var name: String = "b",var age: Int){
    constructor():this(name = "",age  = 0){
        Log.e("//", "first ", )
    }

    constructor(name: String):this(name = name,age = 1){
        Log.e("//", "second", )
    }
}
//class Abc {
//    var name=""
//    constructor(){}
//    constructor(name: String){
//        this.name = name
//    }
//    constructor(age:Int)
//    constructor(name: String,age: Int)
//
//    var age = 10
//}