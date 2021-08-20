package com.example.myfirstapp.googleMaps

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.location.Location
import android.view.animation.LinearInterpolator
import com.example.myfirstapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

object MarkerAnimator {
    var moveingMarker: Marker? = null

    private var animatorSet = AnimatorSet()



    fun updateMarker(newlatlng: LatLng ,mMap: GoogleMap , mapTouched:Boolean) {
        if (moveingMarker != null) {
            val bearingangle = Calculatebearingagle(newlatlng)
            moveingMarker!!.setAnchor(0.5f, 0.5f)
            animatorSet = AnimatorSet()
            animatorSet.playTogether(
                rotateMarker(
                    ((if (java.lang.Float.isNaN(bearingangle)) -1F else bearingangle)),
                    moveingMarker!!.rotation
                ), moveVechile(newlatlng, moveingMarker!!.position)
            )
            animatorSet.start()
        } else AddMarker(newlatlng , mMap)

        if (!mapTouched){
            mMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(newlatlng)
                        .zoom(16f).build()
                )
            )
        }

    }


    private fun AddMarker(initialpos: LatLng , mMap: GoogleMap) {
        val markerOptions = MarkerOptions().position(initialpos).flat(true)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter))
        moveingMarker = mMap.addMarker(markerOptions)
    }

    private fun Calculatebearingagle(newlatlng: LatLng): Float {
        val destinationLoc = Location("service Provider")
        val userLoc = Location("service Provider")
        userLoc.latitude = moveingMarker!!.position.latitude
        userLoc.longitude = moveingMarker!!.position.longitude
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
            moveingMarker!!.rotation = if (-rot > 180) rot / 2 else rot
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
            moveingMarker!!.position = currentPosition
        }
        return valueAnimator
    }

}