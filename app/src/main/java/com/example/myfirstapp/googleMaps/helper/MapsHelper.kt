package com.example.myfirstapp.googleMaps.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.*
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.extra.GpsTracker
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import java.text.DecimalFormat


object MapsHelper {
    val zoom = 10f

    @SuppressLint("MissingPermission")
    fun toCurrentLocation(
        mMap: GoogleMap,
        location: Location?,
        liveLocation: Boolean,
        gpsTracker: GpsTracker,
        callback: (LatLng) -> Unit,
    ) {
        val latLng = LatLng(location!!.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        mMap.animateCamera(cameraUpdate)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!liveLocation) {
                gpsTracker.stopUsingGPS()
            }
            mMap.isMyLocationEnabled = liveLocation
            callback(latLng)
        }, 2000)




    }
    @SuppressLint("MissingPermission")
    fun toCurrentLocation(
        mMap: GoogleMap,
        latlong: LatLng?,
        liveLocation: Boolean,
        gpsTracker: GpsTracker,
        callback: (LatLng) -> Unit,
    ) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlong, zoom)
        mMap.animateCamera(cameraUpdate)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!liveLocation) {
                gpsTracker.stopUsingGPS()
            }
            mMap.isMyLocationEnabled = liveLocation
            callback(latlong!!)
        }, 2000)




    }
    @SuppressLint("MissingPermission")
    fun toCurrentLocation(
        mMap: GoogleMap,
        location: Location?,
        liveLocation: Boolean,
        gpsTracker: GpsTracker,
    ) {
        val latLng = LatLng(location!!.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        mMap.animateCamera(cameraUpdate)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!liveLocation) {
                gpsTracker.stopUsingGPS()
            }
            mMap.isMyLocationEnabled = liveLocation

        }, 2000)
    }

    @SuppressLint("MissingPermission")
    fun toCurrentLocation(
        mMap: GoogleMap,
        location: Location?,
        liveLocation: Boolean
    ) {
        val latLng = LatLng(location!!.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        mMap.animateCamera(cameraUpdate)
        mMap.isMyLocationEnabled = liveLocation
    }

    @SuppressLint("MissingPermission")
    fun moveCamera(latlong: LatLng, previousZoom: Boolean, mMap: GoogleMap) {
        val cameraUpdate: CameraUpdate = if (previousZoom){
            CameraUpdateFactory.newLatLngZoom(latlong, mMap.cameraPosition.zoom)
        }else {
            CameraUpdateFactory.newLatLngZoom(latlong, zoom)
        }
        mMap.animateCamera(cameraUpdate)
    }
    fun moveCamera(
        start: LatLng,
        end: LatLng,
        dynamicDistance: Boolean,
        mMap: GoogleMap,
        context: Context?
    ) {
        var percentage = 0.4
        val bounds: LatLngBounds = LatLngBounds.Builder().include(start).include(end).build()
        val width: Int = context!!.resources.displayMetrics.widthPixels
        val height: Int = context.resources.displayMetrics.heightPixels
        if (dynamicDistance){
            val showDistance = showDistance(start, end)
            if (showDistance < 60){
                Log.e("//", "moveCamera: distance " + showDistance)
                percentage = 0.1
            }
        }
        val padding = (width * percentage).toInt() // offset from edges of the map 15% of screen
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
        mMap.animateCamera(cu)
    }

    fun addMarker(mMap: GoogleMap, latlong: LatLng, animate: Boolean) : Marker{
        val addMarker = mMap.addMarker(MarkerOptions().position(latlong))
        if (animate){
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        latlong.latitude,
                        latlong.longitude
                    ),
                    mMap.cameraPosition.zoom
                )
            )
        }

        return addMarker
    }
    fun addMarker(mMap: GoogleMap, latlong: LatLng, animate: Boolean, icon: BitmapDescriptor) : Marker{
        val addMarker = mMap.addMarker(MarkerOptions().position(latlong).icon(icon).flat(true))

        if (animate){
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        latlong.latitude,
                        latlong.longitude
                    ),
                    mMap.cameraPosition.zoom
                )
            )
        }

        return addMarker
    }
    fun addMarker(
        mMap: GoogleMap,
        sourceLatLng: LatLng,
        animate: Boolean,
        icon: BitmapDescriptor,
        bearing: Boolean,
        destinationLatLng: LatLng
    ) : Marker?{
        if (animate){
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        sourceLatLng.latitude,
                        sourceLatLng.longitude
                    ),
                    mMap.cameraPosition.zoom
                )
            )
        }

        return if (bearing){
            val addMarker = mMap.addMarker(
                MarkerOptions().position(sourceLatLng).icon(icon).flat(
                    true
                ).rotation(
                    calculateBearing(
                        sourceLatLng.latitude,
                        sourceLatLng.longitude,
                        destinationLatLng.latitude,
                        destinationLatLng.longitude
                    )
                )
            )
            addMarker
        }else{
            val addMarker = mMap.addMarker(
                MarkerOptions().position(sourceLatLng).icon(icon).flat(
                    true
                )
            )
            addMarker
        }

    }
    fun calculateBearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val sourceLatLng = LatLng(lat1, lng1)
        val destinationLatLng = LatLng(lat2, lng2)
        return SphericalUtil.computeHeading(sourceLatLng, destinationLatLng).toFloat()
    }

    fun drawPolygonApi(coordinates: List<List<Double>>, mMap: GoogleMap):Polyline {
        val options = PolylineOptions()
        for (i in coordinates.indices){
            options.add(LatLng(coordinates[i][1], coordinates[i][0]))
        }
        options.clickable(true)
            .width(15f)
            .color(Color.BLUE)

        val polyline1 = mMap.addPolyline(options)
        polyline1.tag = "Api"
        return polyline1
    }

    fun updatePolyline(currentLatlng: LatLng, polyline: Polyline) {
        var ixLastPoint = 0
        for (i in 0 until polyline.points.size-1) {
            val point1: LatLng = polyline.points[i]
            val point2: LatLng = polyline.points[i + 1]
            val currentSegment: MutableList<LatLng> = ArrayList()
            currentSegment.add(point1)
            currentSegment.add(point2)
            if (PolyUtil.isLocationOnPath(currentLatlng, currentSegment, true, 100.0)) {
                // save index of last point and exit loop
                ixLastPoint = i
                break
            }
        }

        val pathPoints = polyline.points
        for (i in 0 .. ixLastPoint) {
            pathPoints.removeAt(i)
        }
        polyline.points = pathPoints
    }

    fun calculateDistance(StartP: LatLng, EndP: LatLng): Float {
        val locationA = Location("Source")
        locationA.latitude = StartP.latitude
        locationA.longitude = StartP.longitude
        val locationB = Location("Destination")
        locationB.latitude = EndP.latitude
        locationB.longitude = EndP.longitude
        return locationA.distanceTo(locationB)
    }

    fun addPolygonFake(latlong: ArrayList<LatLng>, mMap: GoogleMap){
        val options = PolylineOptions()
        for (i in latlong){
            options.add(i)
        }
        options.clickable(true)
            .width(17f)
//            .color(0x7F00FF00)
            .color(Color.parseColor("#D2008EFF"))
            .startCap(RoundCap())
            .endCap(RoundCap())
            .geodesic(true)
            .jointType(JointType.BEVEL)

        val polyline1 = mMap.addPolyline(options)
        polyline1.tag = "Api2"
    }

    fun midpoint(lat1: LatLng, lat2: LatLng) = LatLng(
        (lat1.latitude + lat2.latitude) / 2,
        (lat1.longitude + lat2.longitude) / 2
    )

    fun getCenterPointInPolygon(polygon: Polygon): LatLng? {
        val latLngBounds = LatLngBounds.builder()
        for (latLng in polygon.points) {
            latLngBounds.include(latLng)
        }
        return latLngBounds.build().center
    }
    fun getCenterPointInPolygon(latlong: LatLng): LatLng? {
        val latLngBounds = LatLngBounds.builder()
        latLngBounds.include(latlong)
        latLngBounds.include(latlong)
        return latLngBounds.build().center
    }


    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex: Exception) {
            Log.e("//", "getLocationFromAddress: " + ex.message)
        }
        return p1
    }


    fun directionFakePolygon(mMap: GoogleMap, addMarker: Boolean, animate: Boolean) {
        val polyline1 = mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(30.618958967512107, 76.82569429976985),
                    LatLng(30.620082221616403, 76.82473734613687),
                    LatLng(30.62030686024618, 76.82465036495438),
                    LatLng(30.620905889801566, 76.8244764204338),
                    LatLng(30.62157977730924, 76.82447650490302),

                    LatLng(30.655022504280492, 76.82114273182391),
                    LatLng(30.649570126980876, 76.81223313046932),
                )
                .width(15f)
                .color(Color.BLUE)
                .startCap(SquareCap())
                .endCap(SquareCap())
                .jointType(JointType.ROUND)
        )
        polyline1.tag = "A"

        if (addMarker){
            mMap.addMarker(MarkerOptions().position(LatLng(30.618958967512107, 76.82569429976985)))
            mMap.addMarker(MarkerOptions().position(LatLng(30.649570126980876, 76.81223313046932)))
        }
        if (animate){
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    midpoint(
                        LatLng(
                            30.618958967512107,
                            76.82569429976985
                        ),
                        LatLng(30.649570126980876, 76.81223313046932)
                    ),
                    zoom
                )
            )
        }
    }

    fun showDistance(fromMarker: Marker, toMarker: Marker): String? {
        var distance =
            SphericalUtil.computeDistanceBetween(fromMarker.position, toMarker.position)
        var unit = "m"
        if (distance < 1) {
            distance *= 1000.0
            unit = "mm"
        } else if (distance > 1000) {
            distance /= 1000.0
            unit = "km"
        }
        return String.format("%4.3f%s", distance, unit)
    }
    fun showDistanceString(
        currentLatlng: LatLng,
        destinationLatLng: LatLng,
        callback: (Double, Boolean) -> Unit
    ):String{
        var distance = SphericalUtil.computeDistanceBetween(currentLatlng, destinationLatLng)
        var distance2 = SphericalUtil.computeDistanceBetween(currentLatlng, destinationLatLng)
        var unit = " Meter"
        if (distance < 1) {
            distance *= 1000.0
            unit = " mm"
        } else if (distance > 1000) {
            callback(distance, true)
            distance /= 1000.0
            unit = " KM"
            return String.format("%4.3f%s", distance, unit)
        }
        callback(distance, false)
        return String.format("%4.3f%s", distance, unit)
    }
    fun showDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.e(
            "//", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return Radius * c
    }


    fun zoomToMeters(context: Context?, mMap: GoogleMap, latlong: LatLng, meters: Int) {
        val mapHeightInDP = 200
        val r: Resources = context!!.resources
        val mapSideInPixels =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                mapHeightInDP.toFloat(),
                r.displayMetrics
            )
                .toInt()
        val point = LatLng(latlong.latitude, latlong.longitude)
        val latLngBounds = calculateBounds(point, meters.toDouble())
        if (latLngBounds != null) {
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                mapSideInPixels,
                mapSideInPixels,
                10
            )
            if (mMap != null) mMap.animateCamera(cameraUpdate)
        }
    }

    fun getZoomForMetersWide(
        context: Context?,
        googleMap: GoogleMap,
        mapViewWidth: Int,
        latLngPoint: LatLng,
        desiredMeters: Int,
    ) {
        val metrics: DisplayMetrics = context!!.getResources().getDisplayMetrics()
        val mapWidth = mapViewWidth / metrics.density
        val EQUATOR_LENGTH = 40075004
        val TIME_ANIMATION_MILIS = 1500
        val latitudinalAdjustment = Math.cos(Math.PI * latLngPoint.latitude / 180.0)
        val arg = EQUATOR_LENGTH * mapWidth * latitudinalAdjustment / (desiredMeters * 256.0)
        val valToZoom = Math.log(arg) / Math.log(2.0)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLngPoint,
                java.lang.Float.valueOf(valToZoom.toString())
            ), TIME_ANIMATION_MILIS, null
        )
    }

    fun calculateBounds(center: LatLng, radius: Double): LatLngBounds? {
        return LatLngBounds.Builder().include(SphericalUtil.computeOffset(center, radius, 0.0))
            .include(SphericalUtil.computeOffset(center, radius, 90.0))
            .include(SphericalUtil.computeOffset(center, radius, 180.0))
            .include(SphericalUtil.computeOffset(center, radius, 270.0)).build()
    }

    fun boundArea(location: Location?, mMap: GoogleMap, latlongBound: LatLngBounds?) {
        latlongBound?.let {
            mMap.setLatLngBoundsForCameraTarget(latlongBound)
        } ?: {
            val YOUR_CITY_OR_COUNTRY = LatLngBounds(
                LatLng(location!!.latitude - 0.1F, location.longitude - 0.1F),
                LatLng(location.latitude + 0.1F, location.longitude + 0.1F)
            )
            mMap.setLatLngBoundsForCameraTarget(YOUR_CITY_OR_COUNTRY)
        }

    }
    fun boundArea(latlong: LatLng?, mMap: GoogleMap) {
            val YOUR_CITY_OR_COUNTRY = LatLngBounds(
                LatLng(latlong!!.latitude - 0.1F, latlong.longitude - 0.1F),
                LatLng(latlong.latitude + 0.1F, latlong.longitude + 0.1F)
            )
            mMap.setLatLngBoundsForCameraTarget(YOUR_CITY_OR_COUNTRY)
        mMap.setMaxZoomPreference(mMap.maxZoomLevel - 5f)
        mMap.setMinZoomPreference(7f)
    }

    fun resetZoomLevel(mMap: GoogleMap) = mMap.resetMinMaxZoomPreference()

    private fun addCirclePolygon(location: Location?, mMap: GoogleMap) {
        val circleOptions = CircleOptions()
            .center(LatLng(location!!.latitude, location.longitude))
            .radius(10000.0) // In meters
            .strokeWidth(zoom)
            .strokeColor(Color.RED)
            .fillColor(Color.argb(128, 255, 0, 0))
            .clickable(true)

// Get back the mutable Circle
        val circle = mMap.addCircle(circleOptions)
        mMap.setOnCircleClickListener {

        }
    }

    private fun addPolygonExample(mMap: GoogleMap) {
        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        val polyline1 = mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.tag = "A"
        // Style the polyline.
//        stylePolyline(polyline1)

        val polyline2 = mMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-29.501, 119.700),
                    LatLng(-27.456, 119.672),
                    LatLng(-25.971, 124.187),
                    LatLng(-28.081, 126.555),
                    LatLng(-28.848, 124.229),
                    LatLng(-28.215, 123.938)
                )
        )
        polyline2.tag = "B"
//        stylePolyline(polyline2)

        // Add polygons to indicate areas on the map.
        val polygon1 = mMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-27.457, 153.040),
                    LatLng(-33.852, 151.211),
                    LatLng(-37.813, 144.962),
                    LatLng(-34.928, 138.599)
                )
        )
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.tag = "alpha"
        // Style the polygon.
//        stylePolygon(polygon1)

        val polygon2 = mMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-31.673, 128.892),
                    LatLng(-31.952, 115.857),
                    LatLng(-17.785, 122.258),
                    LatLng(-12.4258, 130.7932)
                )
        )
        polygon2.tag = "beta"
//        stylePolygon(polygon2)

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
//        mMap.setOnPolylineClickListener(this)
//        mMap.setOnPolygonClickListener(this)
    }

    @SuppressLint("MissingPermission")
    fun customIconClick(mMap: GoogleMap, context: Context?) {
        val lm = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (myLocation == null) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            val provider = lm.getBestProvider(criteria, true)
            myLocation = lm.getLastKnownLocation(provider!!)
        }

        if (myLocation != null) {
            val userLocation = LatLng(myLocation.latitude, myLocation.longitude)
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(userLocation, 14f),
                1500, null
            )
        } else {
            Log.e(Myconstants.TAG, "customIcon_ClickMethod: m location null")
        }
    }

    fun getPoints() {
        val points: MutableList<LatLng> = ArrayList()
        val locations = ArrayList<Location>()
        for (location in locations) {
            val accuracy = location.accuracy
            points.add(
                SphericalUtil.computeOffset(
                    LatLng(location.latitude, location.longitude),
                    accuracy.toDouble(),
                    0.0
                )
            )
            points.add(
                SphericalUtil.computeOffset(
                    LatLng(location.latitude, location.longitude),
                    accuracy.toDouble(),
                    90.0
                )
            )
            points.add(
                SphericalUtil.computeOffset(
                    LatLng(location.latitude, location.longitude),
                    accuracy.toDouble(),
                    180.0
                )
            )
            points.add(
                SphericalUtil.computeOffset(
                    LatLng(location.latitude, location.longitude),
                    accuracy.toDouble(),
                    270.0
                )
            )
        }
    }
    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }



}