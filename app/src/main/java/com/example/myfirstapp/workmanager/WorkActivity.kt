package com.example.myfirstapp.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityWorkBinding
import java.time.Duration
import java.util.concurrent.TimeUnit

class WorkActivity : AppCompatActivity() {
    lateinit var binding : ActivityWorkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val constraints = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Constraints.Builder()
                .setTriggerContentUpdateDelay(10,TimeUnit.SECONDS)
                .build()
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val myWork = OneTimeWorkRequestBuilder<MyWork>().setConstraints(constraints) .build()

        binding.button4.setOnClickListener {
            WorkManager.getInstance(this).enqueue(myWork)
        }

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(myWork.id).observe(this){
            Toast.makeText(this,it.state.name,Toast.LENGTH_SHORT).show()
        }


    }
}