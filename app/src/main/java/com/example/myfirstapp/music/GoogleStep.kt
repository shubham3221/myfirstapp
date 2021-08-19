package com.example.myfirstapp.music

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myfirstapp.googleMaps.helper.MapsAnimation
import com.example.myfirstapp.googleMaps.helper.MapsHelper
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.GoogleStepFragmentBinding
import com.example.myfirstapp.extra.GpsTracker
import com.example.myfirstapp.extra.LocationChangedListener
import com.example.myfirstapp.music.stepMap.StepService
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class GoogleStep() :Fragment(), OnMapReadyCallback , LocationChangedListener {
    private lateinit var mMap: GoogleMap
    lateinit var binder : GoogleStepFragmentBinding
    lateinit var gpsTracker:GpsTracker
    lateinit var currentLatlng:LatLng
    lateinit var currentLocation: Location
    //service
    var mService: StepService? = null
    var mIsBound: Boolean? = null
    var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StepService.MyStepbinder
            mService = binder.service
            mIsBound = true
            Log.e(Myconstants.TAG, "onServiceConnected: ")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIsBound = false
            Log.e(Myconstants.TAG, "onServiceDisconnected: ")
        }
    }

    val permission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        val empty = it.filterValues { values -> values == false }.isEmpty()
        if (empty){
            gpsTracker = GpsTracker(requireActivity())
            gpsTracker.setLocationCallback(this)
            permissionGiven()
        }
    }

    private fun permissionGiven() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = GoogleStepFragmentBinding.inflate(inflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setMaxZoomPreference(20f)
        val style = MapStyleOptions.loadRawResourceStyle(
                requireActivity(), R.raw.maps_style_night)
        mMap.setMapStyle(style)
        permission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    override fun locationChanged(location: Location?) {
        val latLng = LatLng(location!!.latitude, location.longitude)

        MapsHelper.toCurrentLocation(mMap, location, true, gpsTracker)

            if (::currentLatlng.isInitialized){
                val showDistanceString = MapsHelper.showDistanceString(currentLatlng, latLng) { calSteps, oneKm ->
                    if (oneKm) {
                        var mysteps = calSteps
                        mysteps *= 1000.0
                        val k = calSteps / 0.8
                        binder.steps.text = String.format("%.0f%s", k, " Steps")
                    } else {
                        val k = calSteps / 0.8
                        binder.steps.text = String.format("%.0f%s", k, " Steps")
                    }
                }
                binder.distance.text = showDistanceString

            }else {
                currentLatlng = LatLng(30.3752, 76.7821)
                currentLocation = location
                MapsAnimation.addOverlay(latLng,mMap,requireActivity())

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        gpsTracker?.let {
            it.stopUsingGPS()
            it.stopSelf()
        }
    }
}