package com.example.myfirstapp.extra

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import java.util.*



class GpsTracker2(val context: Context) {
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun checkReq() {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        getLocationUpdates()
    }
    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage("Your location settings is set to Off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
            dialog.create().dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }
    // Start location updates
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        ::fusedLocationClient.isInitialized.let {
            if (it){
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null /* Looper */
                )
            }
        }
    }

    // Stop location updates
    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.e("//", "onLocationResult: 1" )
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    Log.e("//", "onLocationResult: " )
                    /*val location = locationResult.lastLocation
                    Log.e("location", location.toString())*/
                    getAddress(locationResult)
                }
            }
        }
    }

    private fun getAddress(locationResult: LocationResult) {
        val addresses: List<Address>?
        val geoCoder = Geocoder(context, Locale.getDefault())
        addresses = geoCoder.getFromLocation(
            locationResult.lastLocation.latitude,
            locationResult.lastLocation.longitude,
            1
        )
        if (addresses != null && addresses.isNotEmpty()) {
            val address: String = addresses[0].getAddressLine(0)
            val city: String = addresses[0].locality
            val state: String = addresses[0].adminArea
            val country: String = addresses[0].countryName
            val postalCode: String = addresses[0].postalCode
            val knownName: String = addresses[0].featureName
            Log.e("//", "$address $city $state $postalCode $country $knownName")
            stopLocationUpdates()
        }
    }

}