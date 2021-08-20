package com.example.myfirstapp.music.stepMap

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.googleMaps.helper.MapsHelper
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.extentions.toast
import com.example.myfirstapp.notifications.NotificationsUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.util.*

class StepService : LifecycleService(), LocationListener {
    lateinit var mMap: GoogleMap
    private val mBinder: IBinder = MyStepbinder()

    inner class MyStepbinder : Binder() {
        val service: StepService
            get() = this@StepService
    }

    private var locationManager: LocationManager? = null
    var alert: AlertDialog? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var mlocation: Location? = null
    var mlatitude = 0.0
    var mlongitude = 0.0
    var setliveLocation = false
    var currentLatlng: LatLng? = null
    var stepsLiveData = MutableLiveData<String>()
    var distanceLiveData = MutableLiveData<String>()

    //notification
    val notify_Util: NotificationsUtil by lazy {
        NotificationsUtil(this, sound = false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    //fusedlcoation
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return mBinder
    }

    fun showForgroundNotification() {
        startForeground(
            1001, notify_Util.buildNotificationWithService(
                "Counting Start.",
                "Step Counter Service Started!",
                StepService::class.java
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation{
            showForgroundNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.e(TAG, "onStartCommand: ")
        if (Myconstants.STOP_SERVICE == intent?.action) stopStepService()
        return START_NOT_STICKY
    }

    override fun onLocationChanged(location: Location) {
        Log.e(TAG, "onLocationChanged: ")
//        MapsAnimation.addOverlay(LatLng(location.latitude, location.longitude), mMap, this)


        if (currentLatlng != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            var stepCount = 0.0
            MapsHelper.toCurrentLocation(mMap, location, setliveLocation)
            val showDistanceString = MapsHelper.showDistanceString(currentLatlng!!, latLng) { dis, oneKm ->
                stepCount = dis / 0.8
                val speed = String.format(
                    "%.1f%s",
                    ((location.speed * 3600) / 1000),""
                )
                stepsLiveData.value = String.format(
                    "%.0f%s",
                    stepCount,
                    " Steps | Speed: $speed km/h"
                )
//                if (oneKm) {
//                    Log.e(TAG, "1km: " )
//                    var mysteps = dis
//                    mysteps *= 1000.0
//                    stepCount = dis / 0.8
//                    stepsLiveData.value = String.format("%.0f%s", stepCount, " Steps")
//                } else {
//                    stepCount = dis / 0.8
//                    stepsLiveData.value = String.format("%.0f%s", stepCount, " Steps")
//                }
            }

            distanceLiveData.value = showDistanceString
            notify_Util.managerCompat?.notify(
                1001, notify_Util.buildNotificationWithService(
                    "Distance: " + String.format(
                        "%.0f%s",
                        stepCount,
                        " Steps"
                    ), "Step Counter Service Started!", StepService::class.java
                )!!
            )
        }
//        else {
//            currentLatlng = LatLng(30.3752, 76.7821)
//        }
    }


    @SuppressLint("MissingPermission")
    fun getLocation(isSuccess: (Boolean) -> Unit) {
        Log.e(TAG, "getLocation: called")
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGPSEnabled) {
                "Gps not enable".toast(this)
            } else {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                mlocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (mlocation != null) {
                    mlatitude = mlocation!!.latitude
                    mlongitude = mlocation!!.longitude
                    isSuccess(true)
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getLastKnownLocation()
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.toast(this)
        }
    }


    override fun onProviderEnabled(provider: String) {
        Log.e(TAG, "onProviderEnabled: ")
    }

    override fun onProviderDisabled(provider: String) {
        Log.e(TAG, "onProviderDisabled: ")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    fun removeGpsUpdate() {
        if (locationManager != null) {
            Log.e(TAG, "removeGpsUpdate: ")
            locationManager!!.removeUpdates(this)
        } else {
            Log.e(TAG, "removeGpsUpdate: its null")
        }
    }


    fun setCurrentLatlong(latLng: LatLng) {
        this.currentLatlng = latLng
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
                .addOnCompleteListener {
                    it.result ?: getLocation{
                        showForgroundNotification()
                    }
                    it.result.let {
                        if (isGPSEnabled) showForgroundNotification() else getLocation{
                            showForgroundNotification()
                        }
                    }
                }
    }

    fun stopStepService() {
        locationManager?.removeUpdates(this)
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }

    companion object {
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 2 // 10 meters
        const val MIN_TIME_BW_UPDATES = (2000).toLong()
    }

}
