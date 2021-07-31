package com.example.myfirstapp.music

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.StepsLayoutBinding
import com.example.myfirstapp.extentions.askForSinglePermission
import com.example.myfirstapp.music.StepHelper.calculateSpeed
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class StepsFragment : Fragment(), SensorEventListener {
    private var scopeRunning: Boolean = false
    lateinit var binding: StepsLayoutBinding
    private var sensorManager: SensorManager? = null

    private var sensorActive = false

    private var totalSteps = 0f

    private var previousTotalSteps = 0f

    private var stepCount = 0
    private var MagnitudePrevious = 0.0
    var ACCELEROMETER = false
    var count = 0
    var job: Job = Job()
    var timerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = StepsLayoutBinding.inflate(inflater, container, false)
        askForSinglePermission {
            Log.e("//", "onResume: permission")
        }.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        binding.fast.setOnClickListener {
            activateSensor(true)
            calcuateTime()
            binding.pause.text = "pause"
        }

        binding.normal.setOnClickListener {
            activateSensor(false)
            calcuateTime()
            binding.pause.text = "pause"
        }


        binding.pause.setOnClickListener {
            if (StepHelper.running) {
                binding.pause.text = "start"
                unregisterSensor()
            } else {
                binding.pause.text = "pause"
                activateSensor(ACCELEROMETER)
                calcuateTime()

            }
        }


    }

    private fun activateSensor(isAccelerometer: Boolean) {
        if (isAccelerometer) {
            // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
            val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            stepSensor?.let {
                StepHelper.running = true
                ACCELEROMETER = true
                sensorActive = true
                // Rate suitable for the user interface
                sensorManager?.registerListener(this,
                    stepSensor,
                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
            } ?: Toast.makeText(requireActivity(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            stepSensor?.let {
                StepHelper.running = true
                ACCELEROMETER = false
                sensorActive = true
                sensorManager?.registerListener(this,
                    stepSensor,
                    SensorManager.SENSOR_DELAY_FASTEST)
            } ?: Toast.makeText(requireActivity(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calcuateTime() {
        StepHelper.calculateTime(SystemClock.uptimeMillis(), requireContext(), lifecycle) {
            binding.time.text = it
        }
    }

    private fun calculateDistance() {
        StepHelper.calculateDistance {
            binding.distance.text = String.format(resources.getString(R.string.distance), it)
        }
    }
    private fun calcuateSpeed(timestamp: Long) {
        calculateSpeed(timestamp) { mSpeed ->
            binding.speed.text = String.format(resources.getString(R.string.speed),
                mSpeed)
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (sensorActive) {
//            totalSteps = event!!.values[0]
//
//            // Current steps are calculated by taking the difference of total steps
//            // and previous steps
//            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            if (ACCELEROMETER) {
                val x_acceleration: Float? = event?.values?.get(0)
                val y_acceleration: Float? = event?.values?.get(1)
                val z_acceleration: Float? = event?.values?.get(2)
                val magnitude =
                    sqrt((x_acceleration!! * x_acceleration + y_acceleration!! * y_acceleration + z_acceleration!! * z_acceleration).toDouble())
                val steps: Double = magnitude - MagnitudePrevious
                MagnitudePrevious = magnitude
                Log.e("//","mangnitude: $MagnitudePrevious step: ${steps} x: ${x_acceleration} y: ${y_acceleration} z: ${z_acceleration}")


                    if (magnitude >= 13) {
                        if (!scopeRunning){
                            scopeRunning = true
                            job = lifecycleScope.launch {
                                delay(290)
                                totalSteps++
                                binding.tvStepsTaken.text = ("$totalSteps")
                                calcuateSpeed(event.timestamp)
                                calculateDistance()
                                scopeRunning = false
                            }
                        }
                    }

//                if (magnitude >= 13 && count > 2) {
//                    count = 0
//                    totalSteps++
//                    binding.tvStepsTaken.text = ("$totalSteps")
//                    calcuateSpeed(event.timestamp)
//                    calculateDistance()
//                }
//                count++
            } else {
                totalSteps++
                binding.tvStepsTaken.text = ("$totalSteps")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        unregisterSensor()
        super.onDestroy()
    }

    fun unregisterSensor() {
        sensorManager?.unregisterListener(this)
        StepHelper.running = false
        sensorManager?.flush(this)
    }

}