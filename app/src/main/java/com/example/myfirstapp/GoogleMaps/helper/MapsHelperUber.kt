package com.example.myfirstapp.GoogleMaps.helper

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.animation.LinearInterpolator
import com.example.myfirstapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

object MapsHelperUber {
    private lateinit var defaultLocation: LatLng
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null



    fun polylineAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 4000
        return valueAnimator
    }

    fun carAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 3000
        valueAnimator.interpolator = LinearInterpolator()
        return valueAnimator
    }
    fun getCarBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.car)
        return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
    }
    fun getPlainBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.plane)
        return Bitmap.createScaledBitmap(bitmap, 80, 120, false)
    }

    fun getBlackMarkerBitmap(): Bitmap {
        val height = 30
        val width = 30
        val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }
    fun addCarMarkerAndGet(context: Context, map: GoogleMap, latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getCarBitmap(context))
        return map.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }
    fun addPlainMarkerAndGet(context: Context, map: GoogleMap, latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getPlainBitmap(context))
        return map.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = Math.abs(start.latitude - end.latitude)
        val lngDifference: Double = Math.abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(Math.atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(Math.atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(Math.atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(Math.atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }
    fun addOriginDestinationMarkerAndGet(latLng: LatLng, mMap: GoogleMap): Marker {
        val bitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(getBlackMarkerBitmap())
        return mMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }
    fun addBlackMarker(where:LatLng,toWhere:LatLng , mMap: GoogleMap){
        originMarker?.let {
            originMarker = addOriginDestinationMarkerAndGet(where,mMap)
            originMarker?.setAnchor(0.5f, 0.5f)
            destinationMarker = addOriginDestinationMarkerAndGet(toWhere,mMap)
            destinationMarker?.setAnchor(0.5f, 0.5f)
        }
    }
    fun updateCarLocation(context: Context ,latLng: LatLng, mMap: GoogleMap) {

        if (movingCabMarker == null) {
            movingCabMarker = addPlainMarkerAndGet(context, mMap, latLng)
        }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingCabMarker?.position = currentLatLng
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            MapsHelper.moveCamera(currentLatLng!!, true, mMap)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = carAnimator()
            valueAnimator.addUpdateListener { va ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingCabMarker?.position = nextLocation
                    val rotation = getRotation(previousLatLng!!, nextLocation)
                    if (!rotation.isNaN()) {
                        movingCabMarker?.rotation = rotation
                    }
                    movingCabMarker?.setAnchor(0.5f, 0.5f)

                    MapsHelper.moveCamera(currentLatLng!!, true, mMap)
                }
            }
            valueAnimator.start()
        }
    }


}