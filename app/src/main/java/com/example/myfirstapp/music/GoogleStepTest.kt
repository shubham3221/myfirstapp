package com.example.myfirstapp.music

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.example.myfirstapp.extentions.toast
import com.example.myfirstapp.music.stepMap.MapReady
import com.example.myfirstapp.music.stepMap.MapsSetupFramgent
import com.example.myfirstapp.music.stepMap.StepService
import com.google.android.gms.maps.GoogleMap
import java.lang.Exception
import java.util.*
import android.os.*

import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.googleMaps.MyLocationProvider
import com.example.myfirstapp.googleMaps.MarkerAnimator
import com.example.myfirstapp.googleMaps.helper.MapsHelper
import com.example.myfirstapp.googleMaps.helper.MapsHelperUber
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.google_step_fragment.*

class GoogleStepTest:Fragment(R.layout.google_step_fragment) , MapReady ,LocationListener{
    private var started: Boolean = false
    private var count: Int = 0
    private var mapTouched: Boolean = false
    private var mMap: GoogleMap? = null

    private val points: ArrayList<LatLng> = ArrayList()
    private val alLatLng: ArrayList<LatLng> = ArrayList()
    private var locationManager: LocationManager? = null
    private var currentLatlng:LatLng? = null
    private var previousLatLng:LatLng? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsSetupFramgent(this).initMap()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
//        getLocation()

        MyLocationProvider(requireActivity(),this,null).initLocation()

        mMap!!.setOnMapClickListener {
            mapTouched = true
        }

        startserice.text = "start"
        startserice.setOnClickListener {
            started = true
        }
    }
    override fun onLocationChanged(location: Location) {
        currentLatlng = LatLng(location.latitude, location.longitude)
        if (previousLatLng!=null){
            plotPolyline(previousLatLng?.latitude ,previousLatLng?.longitude ,currentLatlng?.latitude,currentLatlng?.longitude)
            previousLatLng = null
            points.clear()
        }
        if (started){
            points.add(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )
            SendNextPoints()
            MapsHelper.addPolygonFake(points,mMap!!)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        try {
            locationManager = requireActivity().getSystemService(LifecycleService.LOCATION_SERVICE) as LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                StepService.MIN_TIME_BW_UPDATES,
                StepService.MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                this
            )
            locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.message?.toast(requireActivity())
        }
    }

    fun addCurvePolyline(start:LatLng,end: LatLng){
        var cLat: Double = (start.latitude + end.latitude) / 2
        var cLon: Double = (start.longitude + end.longitude) / 2

        //add skew and arcHeight to move the midPoint

        //add skew and arcHeight to move the midPoint
        if (Math.abs(start.longitude - end.longitude) < 0.0001) {
            cLon -= 0.0195
        } else {
            cLat += 0.0195
        }

        val tDelta = 1.0 / 50
        run {
            var t = 0.0
            while (t <= 1.0) {
                val oneMinusT = 1.0 - t
                val t2 = Math.pow(t, 2.0)
                val lon: Double =
                    oneMinusT * oneMinusT * start.longitude + 2 * oneMinusT * t * cLon + t2 * end.longitude
                val lat: Double =
                    oneMinusT * oneMinusT * start.latitude + 2 * oneMinusT * t * cLat + t2 * end.latitude
                alLatLng.add(LatLng(lat, lon))
                t += tDelta
            }
        }

        // draw polyline

        // draw polyline
        val line = PolylineOptions()
        line.width(15f)
        line.color(Color.RED)
        line.addAll(alLatLng)
        mMap!!.addPolyline(line)
    }
    private fun plotPolyline(
        startLat: Double?,
        startLon: Double?,
        markerLat: Double?,
        markerLon: Double?
    ) {
        if (startLat == null || startLon == null || markerLat == null || markerLon == null) {
            return
        }
        var startPoint = LatLng(startLat, startLon)
        var endPoint = LatLng(markerLat, markerLon)
        val distance = SphericalUtil.computeDistanceBetween(startPoint, endPoint)
        val midPoint = SphericalUtil.interpolate(startPoint, endPoint, 0.5)
        val midToStartLocHeading = SphericalUtil.computeHeading(midPoint, startPoint)
        val controlPointAngle = 360.0 - (180.0 - midToStartLocHeading)
        val controlPoint = SphericalUtil.computeOffset(midPoint, distance / 2.0, controlPointAngle)
        var t = 0.0
        val polylineOptions = PolylineOptions()

        while (t <= 1.00) {
            val oneMinusT = 1.0 - t
            val lon: Double =
                oneMinusT * oneMinusT * startLon + 2 * oneMinusT * t * controlPoint.longitude + t * t * markerLon
            val lat: Double =
                oneMinusT * oneMinusT * startLat + 2 * oneMinusT * t * controlPoint.latitude + t * t * markerLat
            polylineOptions.add(LatLng(lat, lon))
            t += 0.05
        }

        polylineOptions.add(endPoint)

        // Draw polyline
//        polyline?.remove()
        var pattern = listOf(Gap(10.0f), Dash(10.0f))
        mMap?.addPolyline(
            polylineOptions.width(10f).pattern(pattern)
                .color(Color.RED)
                .geodesic(false)
        )
    }

    private fun SendNextPoints() {
        if (points.isNotEmpty()) MarkerAnimator.updateMarker(points.last(),mMap!!,mapTouched) // taking the points f rom head of the queue.
        Log.e(TAG, "SendNextPoints: ${points.size}")
    }

    override fun onProviderEnabled(provider: String) {
        Log.e(Myconstants.TAG, "onProviderEnabled: ")
    }

    override fun onProviderDisabled(provider: String) {
        Log.e(Myconstants.TAG, "onProviderDisabled: ")
        previousLatLng = points.last()



    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onDestroy() {
        super.onDestroy()
        points.clear()
        mMap?.clear()
//        locationManager!!.removeUpdates(this)
    }
}