package com.example.myfirstapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityServiceBinding
import com.example.myfirstapp.googlesdk.GoogleActivity
import com.example.myfirstapp.services.adapter.MyAdapter
import com.example.myfirstapp.twitterLogin.TwitterActivity
import kotlinx.android.synthetic.main.activity_service.*
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


        binding.callAdapter.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            startService(intent)
        }

        start_service_notify.setOnClickListener {
            val intent = Intent(this,MyService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
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
