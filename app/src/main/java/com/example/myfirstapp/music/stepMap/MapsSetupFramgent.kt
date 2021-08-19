package com.example.myfirstapp.music.stepMap

import android.Manifest
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.extra.BasicHelper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task


interface MapReady{
    fun onMapReady(p0: GoogleMap)
}

class MapsSetupFramgent(val fragment: Fragment):OnMapReadyCallback {

    var mapReady: MapReady = fragment as MapReady
    val context = fragment.requireActivity()
    lateinit var mMap: GoogleMap

    //permission
    val permission = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        val result = it.filterValues { values -> values == false }
        if (result.isEmpty()){
            checkGPS_Permission()
        } else {
            BasicHelper.showDialogPermissionFragment(context, result.keys.first(), fragment){ isPermissionDenied->
//                if (success) startwork() else "wrong".toast(context)
                if (isPermissionDenied){
                    Log.e(TAG, "permission denied: ")
                    requestPermissions()
                }
            }
        }
    }


    //permission alternative
    val gpsPermission: ActivityResultLauncher<IntentSenderRequest> =
            fragment.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                when (it.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        Toast.makeText(context, "Gps On", Toast.LENGTH_SHORT).show()
                        startwork()
                    }
                    AppCompatActivity.RESULT_CANCELED -> {
                        checkGPS_Permission()
                    }
                }
            }

    private fun startwork() {
        mapReady.onMapReady(mMap)
        Log.e(TAG, "startwork: ")
    }

    fun initMap(){
        val supportMapFragment = fragment.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
//        mMap.setMaxZoomPreference(20f)
//        val style = MapStyleOptions.loadRawResourceStyle(
//            context, R.raw.maps_style_night
//        )
//        mMap.setMapStyle(style)
        requestPermissions()
    }
    fun requestPermissions(){
        permission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    fun checkGPS_Permission() {
        var intentSenderRequest: IntentSenderRequest? = null
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
                settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(context) {
            Log.e("//", "OnSuccess")
            startwork()
        }
        task.addOnFailureListener(context) { e ->
            Log.e("//", "GPS off")
            // GPS off
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        e as ResolvableApiException
                        intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                        gpsPermission.launch(intentSenderRequest)
                        intentSenderRequest = null
                    } catch (e1: IntentSender.SendIntentException) {
                        e1.printStackTrace()
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(context, "Turn off airplain mode", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}