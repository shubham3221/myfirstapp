package com.example.myfirstapp.services

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirstapp.databinding.ActivityServiceBinding


data class Pojo(val name: String = "default", val number: String = "0")

class ServiceActivity : AppCompatActivity() {

    lateinit var binding: ActivityServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_service)
//        val pojo = Pojo("shubham", "0")
//        binding.pojo = pojo
//
//
//        binding.callAdapter.setOnClickListener {
//            val intent = Intent(this,MyService::class.java)
//            startService(intent)
//        }
//
//        start_service_notify.setOnClickListener {
//            val intent = Intent(this,MyService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent)
//            } else {
//                startService(intent)
//            }
//        }
//
//    }
//
//    private fun setupRecycleView() {
//        val pojo = Pojo("ram", "1")
//        var list = ArrayList<Pojo>()
//        list.add(pojo)
//        val adapter = MyAdapter(list)
//        binding.mRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
//        binding.mRecycler.adapter = adapter
//
//    }
    }
}
