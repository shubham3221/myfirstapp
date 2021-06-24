package com.example.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.twitterLogin.Myinterface
import kotlinx.android.synthetic.main.demo_layout_mainactivity.*
interface MyCallback{
    fun onClick()
    fun onClickParams(name:String)
}

class Demoactivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_layout_mainactivity)

        button.setOnClickListener {
            startActivity(Intent(this,Lastscreen::class.java))
        }
        abc(object :MyCallback{
            override fun onClick() {
                TODO("Not yet implemented")
            }

            override fun onClickParams(name: String) {
                TODO("Not yet implemented")
            }

        })

//        arrayDemo()
        method1("shubham") { param: Boolean, age: Int ->
//            Log.e(TAG, "onCreate: "+b+" "+i)
        }
    }

    fun method1(name: String,callback : (param : Boolean , param2 : Int) -> Unit){
        Log.e(TAG, "method1: "+name )
        callback(true,1)
        Thread{
            Thread.sleep(2000)
            callback(false,2)
        }.start()
    }

    fun abc(callback: MyCallback){
        Thread {
            Thread.sleep(2000)
            callback.onClickParams("shubham")
        }.start()
    }

    private fun arrayDemo() {
        val name: String = "".let {
            "s"
        }
        Log.e(TAG, "onCreate: " + name)

        var arr2 = emptyArray<String>()
        var arr3 = arrayOf<String>()
        var arr4 = mutableListOf<String>()
        var arr5 = listOf<String>("s", "f")
        //        arr5[0] = "s"
        arr4.add("s")

        var demo = "Demo"
        val all = arr2.apply {
            val demo = "demo2"
        }
        Log.e(TAG, "onCreate: " + all)

        repeat(arr2.count()) {

        }
        arr2.forEachIndexed { index, value ->

        }


        //maps
        var map = mutableMapOf<String, String>()
        map.put("key1", "value1")
        map.put("key2", "value2")

        val mapValues = map.mapValues { (key, value) ->
            Log.e(TAG, "arrayDemo: $value" )
        }



        Log.e(TAG, "onCreate: " + map.get("key2"))


        Log.e(TAG, "onCreate: " + arr3.size)


        var arr = arrayOf("one", "two", "three")
        arr[0] = "two"
        Log.e(TAG, "onCreate: ${
            arr.indices.let {
                it.map {
                    return@map arr[it]
                }
            }
        }")
    }
}