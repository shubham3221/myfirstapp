package com.example.myfirstapp.extra

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.music.GoogleStep
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task

class GpsTracker(context: Context) : Service(), LocationListener {
    private val mContext: Context
    var alert: AlertDialog? = null

    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false
    var mlocation : Location? = null
    var mlatitude = 0.0
    var mlongitude = 0.0

    // Declaring a Location Manager
    private var locationManager: LocationManager? = null
    lateinit var changedListener: LocationChangedListener


    companion object {
        // The minimum distance to change Updates in meters
//        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

//        // The minimum time between updates in milliseconds
//        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
//                ).toLong()
        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (4_000 // 1 minute
                ).toLong()
    }

    init {
        Log.e("//", "GpsTracker: ")
        mContext = context
//        changedListener = context as LocationChangedListener
        getLocation()
    }
    fun setLocationCallback(myInt: LocationChangedListener){
        changedListener = myInt
    }



    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = mContext
                .getSystemService(LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    if (locationManager != null) {
                        mlocation = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (mlocation != null) {
                            mlatitude = mlocation!!.latitude
                            mlongitude = mlocation!!.longitude
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else if (isGPSEnabled) {
                    if (mlocation == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        if (locationManager != null) {
                            mlocation = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (mlocation != null) {
                                mlatitude = mlocation!!.latitude
                                mlongitude = mlocation!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("//", "getLocation: exception try cattch: " + e.message)
        }
        return mlocation
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            Log.e(TAG, "stopUsingGPS: " )
            locationManager!!.removeUpdates(this@GpsTracker)
        }else{
            Log.e(TAG, "stopUsingGPS: its null" )
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (mlocation != null) {
            mlatitude = mlocation!!.latitude
        }

        // return latitude
        return mlatitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (mlocation != null) {
            mlongitude = mlocation!!.longitude
        }

        // return longitude
        return mlongitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    fun showSettingsAlert() {
        if (alert == null) {
            val builder = AlertDialog.Builder(mContext)
            // Setting Dialog Title
            builder.setTitle("Turn On GPS in settings")

            // Setting Dialog Message
            builder
                .setMessage("GPS is not enabled. Do you want to go to settings menu?")

            // On pressing Settings button
            builder.setPositiveButton("Settings"
            ) { dialog, which ->
                alert = null
                val intent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            }

            // on pressing cancel button
            builder.setNegativeButton("Cancel"
            ) { dialog, which ->
                alert = null
                dialog.cancel()
            }
            alert = builder.create()
            if (!alert!!.isShowing()) alert!!.show()
        }
    }

    override fun onLocationChanged(location: Location) {
        changedListener.locationChanged(location)
    }

    override fun onProviderDisabled(provider: String) {
        Log.e("//", "onProviderDisabled: $provider")
        showSettingsAlert()
    }

    override fun onProviderEnabled(provider: String) {
        Log.e("//", "onProviderEnabled: ")
        if (alert != null && alert!!.isShowing) {
            alert!!.dismiss()
            alert = null
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    //alternative method
    @SuppressLint("MissingPermission")
    fun getLocation(mMap:GoogleMap) {
        FusedLocationProviderClient(this).lastLocation
            .addOnCompleteListener { task: Task<Location?> ->
                val location = task.result
                if (location != null && mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(location.latitude, location.longitude), 15f))
                    mMap.isMyLocationEnabled = true
                    Handler(Looper.getMainLooper()).postDelayed({ getLocation() }, 5000)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("//", "onDestroy: ")
        stopUsingGPS()
    }


}