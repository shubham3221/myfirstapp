package com.example.myfirstapp.services

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityServiceBinding
import com.example.myfirstapp.services.adapter.MyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class Pojo(val name: String = "default", val number: String = "0")

class ServiceActivity : AppCompatActivity() {

    lateinit var binding: ActivityServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service)
        val pojo = Pojo("shubham", "0")
        binding.pojo = pojo

        setupRecycleView()
        binding.callAdapter.setOnClickListener {
            setupRecycleView()
        }

    }

    private fun setupRecycleView() {
        val pojo = Pojo("ram", "1")
        var list = ArrayList<Pojo>()
        list.add(pojo)
        val adapter = MyAdapter(list)
        binding.mRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.mRecycler.adapter = adapter

    }
}
