package com.example.myfirstapp.music.helper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.example.myfirstapp.Myconstants.Companion.TAG
import java.util.*
import kotlin.collections.ArrayList


class StepDetectorStackoverflow(val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null

    fun registerSensor(){
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val stepSensor2 = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager?.registerListener(this,stepSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
        sensorManager?.registerListener(this,stepSensor2, SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
    }
    fun unregisterSensor(){
        sensorManager?.unregisterListener(this)

    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val values = sensorEvent.values
        val sensor: Sensor = sensorEvent.sensor
        if (sensor.type === Sensor.TYPE_MAGNETIC_FIELD) {
            magneticDetector(values, sensorEvent.timestamp / (500 * 10 xor 6))
        }
        if (sensor.type === Sensor.TYPE_ACCELEROMETER) {
            accelDetector(values, sensorEvent.timestamp / (500 * 10 xor 6))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private val mAccelDataBuffer = ArrayList<FloatArray>()
    private var mMagneticFireData = ArrayList<Long>()
    private var mLastStepTime: Long? = null
    private val mAccelFireData = ArrayList<Pair>()
    private fun accelDetector(detectedValues: FloatArray, timeStamp: Long) {
        val currentValues = FloatArray(3)
        for (i in currentValues.indices) {
            currentValues[i] = detectedValues[i]
        }
        mAccelDataBuffer.add(currentValues)
        if (mAccelDataBuffer.size > MAX_BUFFER_SIZE) {
            var avgGravity = 0.0
            for (values in mAccelDataBuffer) {
                avgGravity += Math.abs(Math.sqrt((
                        values[0] * values[0] + values[1] * values[1] + values[2] * values[2]).toDouble()) - SensorManager.STANDARD_GRAVITY)
            }
            avgGravity /= mAccelDataBuffer.size
            if (avgGravity >= MIN_GRAVITY && avgGravity < MAX_GRAVITY) {
                mAccelFireData.add(Pair(timeStamp, true))
                Log.e(TAG, "accelDetector: ${mAccelFireData.size}  atFirstPosition: ${mAccelFireData[0].first}" )
            } else {
                mAccelFireData.add(Pair(timeStamp, false))
            }
            if (mAccelFireData.size >= Y_DATA_COUNT) {
                checkData(mAccelFireData, timeStamp)
                mAccelFireData.removeAt(0)
            }
            mAccelDataBuffer.clear()
        }
    }

    private fun checkData(accelFireData: ArrayList<Pair>, timeStamp: Long) {
        var stepAlreadyDetected = false
        var iterator: Iterator<Pair> = accelFireData.iterator()
        while (iterator.hasNext() && !stepAlreadyDetected) {
            stepAlreadyDetected = iterator.next().first == mLastStepTime
        }
        if (!stepAlreadyDetected) {
            var firstPosition: Int = Collections.binarySearch(mMagneticFireData, accelFireData[0].first)
            val secondPosition: Int = Collections
                    .binarySearch(mMagneticFireData, accelFireData[accelFireData.size - 1].first - 1)
            if (firstPosition > 0 || secondPosition > 0 || firstPosition != secondPosition) {
                if (firstPosition < 0) {
                    firstPosition = -firstPosition - 1
                }
                if (firstPosition < mMagneticFireData.size && firstPosition > 0) {
                    mMagneticFireData = ArrayList(
                            mMagneticFireData.subList(firstPosition - 1, mMagneticFireData.size))
                }
                iterator = accelFireData.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next().second) {
                        mLastStepTime = timeStamp
                        accelFireData.removeAt(accelFireData.size - 1)
                        accelFireData.add(Pair(timeStamp, false))
//                        onStep()
                        Log.e(TAG, "on step: " )
                        break
                    }
                }
            }
        }
    }

    private var mLastDirections = 0f
    private var mLastValues = 0f
    private val mLastExtremes = FloatArray(2)
    private var mLastType: Int? = null
    private val mMagneticDataBuffer = ArrayList<Float>()
    private fun magneticDetector(values: FloatArray, timeStamp: Long) {
        mMagneticDataBuffer.add(values[2])
        if (mMagneticDataBuffer.size > MAX_BUFFER_SIZE) {
            var avg = 0f
            for (i in 0 until mMagneticDataBuffer.size) {
                avg += mMagneticDataBuffer[i]
            }
            avg /= mMagneticDataBuffer.size
            val direction = (if (avg > mLastValues) 1 else if (avg < mLastValues) -1 else 0).toFloat()
            if (direction == -mLastDirections) {
                // Direction changed
                val extType = if (direction > 0) 0 else 1 // minumum or maximum?
                mLastExtremes[extType] = mLastValues
                val diff = Math.abs(mLastExtremes[extType] - mLastExtremes[1 - extType])
                if (diff > 8 && (null == mLastType || mLastType != extType)) {
                    mLastType = extType
                    mMagneticFireData.add(timeStamp)
                }
            }
            mLastDirections = direction
            mLastValues = avg
            mMagneticDataBuffer.clear()
        }
    }

    class Pair(var first: Long, var second: Boolean) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readLong(),
                parcel.readByte() != 0.toByte()) {
        }

        override fun equals(o: Any?): Boolean {
            return if (o is Pair) {
                first == o.first
            } else false
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(first)
            parcel.writeByte(if (second) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Pair> {
            override fun createFromParcel(parcel: Parcel): Pair {
                return Pair(parcel)
            }

            override fun newArray(size: Int): Array<Pair?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        const val MAX_BUFFER_SIZE = 5
        private const val Y_DATA_COUNT = 4
        const val MIN_GRAVITY = 2.0
        private const val MAX_GRAVITY = 1200.0
    }
}