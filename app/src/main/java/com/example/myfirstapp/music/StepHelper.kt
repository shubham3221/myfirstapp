package com.example.myfirstapp.music

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.coroutineScope
import com.example.myfirstapp.R
import kotlinx.coroutines.*

object StepHelper: LifecycleObserver {
    private var totalSteps: Long = 0
    private var stepTimestamp: Long = 0
    private lateinit var scope: LifecycleCoroutineScope
    var STOPTIMER = false
    var elsp = 0L
    var updated = 0L
    var timeInMilliseconds: Long = 0
    var running = false


    //Calculated the amount of steps taken per minute at the current rate
    fun calculateSpeed(eventTimeStamp: Long , callback:(Int)-> Unit) {
        val timestampDifference: Long = eventTimeStamp - stepTimestamp
        stepTimestamp = eventTimeStamp
        val stepTime = timestampDifference / 1000000000.0
        val speed = (60 / stepTime).toInt()
        callback(speed)
    }

    fun calculateDistance(callback:(String)->Unit) {
        totalSteps += 2

        //Distance calculation
        val mdistance = totalSteps * 0.8 //Average step length in an average adult

        val distanceString = String.format("%.2f", mdistance)
        callback(distanceString)
    }


    //Runnable that calculates the elapsed time since the user presses the "start" button
    fun calculateTime(
        startTime: Long,
        context: Context,
        lifecycle: Lifecycle,
        time: (String) -> Unit,
    ) {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope


       scope.launch {
           Log.e("//", "calculateTime: " + startTime)
           while (isActive and running){
               delay(1000)
               timeInMilliseconds = SystemClock.uptimeMillis() - startTime
               updated = timeInMilliseconds+elsp
               var seconds = (updated / 1000)
               var minutes = seconds / 60
               val hours = minutes / 60
               seconds = seconds % 60
               minutes = minutes % 60
               val timeString = String.format("%d:%s:%s",
                   hours,
                   String.format("%02d", minutes),
                   String.format(
                       "%02d",
                       seconds))
               val format = String.format(context.resources.getString(R.string.time), timeString)
               time(format)
           }
           elsp+=timeInMilliseconds
       }

    }
}