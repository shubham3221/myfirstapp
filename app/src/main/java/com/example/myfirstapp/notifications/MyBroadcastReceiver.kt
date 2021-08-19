package com.example.myfirstapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.music.stepMap.StepService
import org.greenrobot.eventbus.EventBus
data class JobData(var command:String,var id:Int)
data class ProgressUpdates(var process:Int)
class MyBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent!!.action){
            "resume" -> {
                EventBus.getDefault().post(JobData("resume",intent.getIntExtra("id",0)))
                Toast.makeText(context,"resume",Toast.LENGTH_SHORT).show()
            }
            "pause" -> {
                EventBus.getDefault().post(JobData("pause",intent.getIntExtra("id",0)))
                Toast.makeText(context,"pause",Toast.LENGTH_SHORT).show()
            }
            "stop" -> {
                Log.e(TAG, "onReceive: " )
                EventBus.getDefault().post(JobData("stop", intent.getIntExtra("id", 0)))
                Toast.makeText(context, "stop", Toast.LENGTH_SHORT).show()
            }
            "stop-service" -> {
                EventBus.getDefault().post(JobData("stop_service", -1))
            }
            "progress" -> {
                EventBus.getDefault().post(ProgressUpdates(intent.getIntExtra("progress",0)))
            }
            else -> {
                EventBus.getDefault().post(JobData("nothing",intent.getIntExtra("id",0)))
                Toast.makeText(context,"nothing received",Toast.LENGTH_SHORT).show()
            }
        }
    }
}