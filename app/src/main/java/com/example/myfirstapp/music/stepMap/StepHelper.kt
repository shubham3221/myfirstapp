package com.example.myfirstapp.music.stepMap

import android.content.Context
import android.hardware.SensorEvent
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.*
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import kotlinx.coroutines.*
import kotlin.math.sqrt

object StepHelper: LifecycleObserver {
    private var prev: Double=0.0
    private var stepTimestamp: Long = 0
    private lateinit var scope: LifecycleCoroutineScope
    var STOPTIMER = false
    var elsp = 0L
    var updated = 0L
    var timeInMilliseconds: Long = 0
    var running = false
    var mdistance = 0.0
    private var currentvectorSum: Float=0f
    private var inStep: Boolean = false
    private var totalSteps = 0f

    private var scopeRunning: Boolean = false
    
    init {
        Log.e(TAG, "init: ", )
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(lifecycleOwner: LifecycleOwner) {

        Log.e(TAG, "onStop: ", )

        // Cast lifecycleOwner to Fragment or Activity and use isChangingConfiguration
    }




    //Calculated the amount of steps taken per minute at the current rate
    fun calculateSpeed(eventTimeStamp: Long , callback:(Int)-> Unit) {
        val timestampDifference: Long = eventTimeStamp - stepTimestamp
        stepTimestamp = eventTimeStamp
        val stepTime = timestampDifference / 1000000000.0
        val speed = (60 / stepTime).toInt()
        callback(speed)
    }

    fun calculateSpeed2(eventTimeStamp: Long , callback:(String)-> Unit) {
        val l = mdistance / timeInMilliseconds
        Log.e(TAG, "calculateSpeed2: $l")
        callback(l.toString().substring(0,3))
    }

    fun calculateDistance(steps:Float,callback:(String)->Unit) {
//        totalSteps += 2

        //Distance calculation
        val distance = steps * 0.8 //Average step length in an average adult
        mdistance = distance
        Log.e(Myconstants.TAG, "steps: ${mdistance /0.8}", )

        val distanceString = String.format("%.2f", distance)
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
            for (i in 0..50){
                delay(1000)
                Log.e(TAG, "calculateTime: $i" )
            }
        }


       scope.launch {
           Log.e("//", "calculateTime: " + startTime)
           while (isActive and running){
               delay(1000)
               timeInMilliseconds = SystemClock.uptimeMillis() - startTime
               updated = timeInMilliseconds + elsp
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
           elsp += timeInMilliseconds
       }

    }

    fun countStepsMethod_2(event: SensorEvent?, callback: (Float) -> Unit) {
        val x = event!!.values[0]
        val y = event.values[1]
        val z = event.values[2]

        currentvectorSum = x * x + y * y + z * z
        if (currentvectorSum < 100 && !inStep) {
            inStep = true
        }
        if (currentvectorSum > 125 && inStep) {
            inStep = false
            totalSteps++
            callback(totalSteps)
        }
    }

    fun stepsCouterMethod_1(event: SensorEvent? , callback: (Float) -> Unit) {
        val x: Float? = event?.values?.get(0)
        val y: Float? = event?.values?.get(1)
        val z: Float? = event?.values?.get(2)
        val magnitude =
            sqrt((x!! * x + y!! * y + z!! * z).toDouble())
        val steps: Double = magnitude - prev
        prev = magnitude
        Log.e("//", "mangnitude: $prev step: ${steps} x: ${x} y: ${y} z: ${z}")

        if (magnitude >= 13) {
            if (!scopeRunning) {
                scopeRunning = true
                scope.launch {
                    delay(290)
                    totalSteps++
                    scopeRunning = false
                    callback(totalSteps)
                }
            }
        }
    }

}