package com.example.myfirstapp.music

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.example.myfirstapp.extentions.toast
import com.example.myfirstapp.music.stepMap.MapReady
import com.example.myfirstapp.music.stepMap.MapsSetupFramgent
import com.example.myfirstapp.music.stepMap.StepService
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.Float.isNaN
import java.util.*
import java.util.concurrent.TimeUnit

class GoogleStepTest:Fragment(R.layout.google_step_fragment) , MapReady ,LocationListener{
    private var mMap: GoogleMap? = null

    private var marker: Marker? = null

    private val points: Queue<LatLng> = LinkedList()

    private var animatorSet = AnimatorSet()

    private var locationManager: LocationManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsSetupFramgent(this).initMap()

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        getLocation {
            Log.e("//", "ready to go: ")
        }
    }
    override fun onLocationChanged(location: Location) {
        points.add(
            LatLng(
                location.latitude,
                location.longitude
            )
        )
        SendNextPoints()
    }
    @SuppressLint("MissingPermission")
    fun getLocation(isSuccess: (Boolean) -> Unit) {
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




    private fun SendNextPoints() {
        if (!animatorSet.isRunning && !points.isEmpty()) UpdateMarker(points.poll()!!) // taking the points f rom head of the queue.
    }

    private fun UpdateMarker(newlatlng: LatLng) {
        if (marker != null) {
            val bearingangle = Calculatebearingagle(newlatlng)
            marker!!.setAnchor(0.5f, 0.5f)
            animatorSet = AnimatorSet()
            animatorSet.playTogether(
                rotateMarker(
                    ((if (isNaN(bearingangle)) -1F else bearingangle) as Float),
                    marker!!.rotation
                ), moveVechile(newlatlng, marker!!.getPosition())
            )
            animatorSet.start()
        } else AddMarker(newlatlng)
        mMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(newlatlng)
                    .zoom(16f).build()
            )
        )
    }


    private fun AddMarker(initialpos: LatLng) {
        val markerOptions = MarkerOptions().position(initialpos).flat(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter))
        marker = mMap!!.addMarker(markerOptions)
    }

    private fun Calculatebearingagle(newlatlng: LatLng): Float {
        val destinationLoc = Location("service Provider")
        val userLoc = Location("service Provider")
        userLoc.latitude = marker!!.position.latitude
        userLoc.longitude = marker!!.position.longitude
        destinationLoc.latitude = newlatlng.latitude
        destinationLoc.longitude = newlatlng.longitude
        return userLoc.bearingTo(destinationLoc)
    }

    @Synchronized
    fun rotateMarker(toRotation: Float, startRotation: Float): ValueAnimator? {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 1555
        valueAnimator.addUpdateListener { valueAnimator ->
            val t = valueAnimator.animatedValue.toString().toFloat()
            val rot = t * toRotation + (1 - t) * startRotation
            marker!!.rotation = if (-rot > 180) rot / 2 else rot
        }
        return valueAnimator
    }


    @Synchronized
    fun moveVechile(finalPosition: LatLng, startPosition: LatLng): ValueAnimator? {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener { valueAnimator ->
            val t = valueAnimator.animatedValue.toString().toFloat()
            val currentPosition = LatLng(
                startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                startPosition.longitude * (1 - t) + finalPosition.longitude * t
            )
            marker!!.position = currentPosition
        }
        return valueAnimator
    }


    override fun onProviderEnabled(provider: String) {
        Log.e(Myconstants.TAG, "onProviderEnabled: ")
    }

    override fun onProviderDisabled(provider: String) {
        Log.e(Myconstants.TAG, "onProviderDisabled: ")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}