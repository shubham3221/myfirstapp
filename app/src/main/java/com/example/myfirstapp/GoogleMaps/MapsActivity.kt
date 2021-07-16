package com.example.myfirstapp.GoogleMaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender.SendIntentException
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.utils.GpsTracker
import com.example.myfirstapp.utils.LocationChangedListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationChangedListener ,
    GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {
    private lateinit var locationManager: LocationManager
    var mapFragment: SupportMapFragment? = null
    private lateinit var mMap: GoogleMap
    var gpsTracker: GpsTracker? = null

    val getCheckLocationOK: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            when(it.resultCode){
               RESULT_OK ->  {
                   Toast.makeText(applicationContext, "Gps On", Toast.LENGTH_SHORT).show()
                   gpsTracker = GpsTracker(this)
               }
                RESULT_CANCELED -> {
                    checkGPS()
                }
            }
        }

    //get places json from raw folder
    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    //change marker icon
    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.black)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_baseline_star_24, color)
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val check = it.filterValues { value -> !value }
            if (check.isNullOrEmpty()) permissionGiven()
        }

    @SuppressLint("MissingPermission")
    private fun permissionGiven() {
        mMap.isMyLocationEnabled = true
        mMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
        checkGPS()

        LocationMarkerWithDestroy.registerLifecycleOwner(lifecycle)
        mMap.setOnMapClickListener {
            mMap.clear()
            val addMarker = mMap.addMarker(MarkerOptions().position(it))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude),
                mMap.cameraPosition.zoom))
            LocationMarkerWithDestroy.getAddress(this@MapsActivity, it) { address ->
                address.apply {
                    addMarker.tag = Place(city, it, address.address, 4F)
                    location_TextView.text =
                        "${address.address} $city $country $knownName $postalCode $state"
                }
            }
        }

        custom_location_icon.setOnClickListener {
            customIcon_ClickMethod()
        }


    }

    private fun addPolygon() {
        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        val polyline1 = mMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(-35.016, 143.321),
                LatLng(-34.747, 145.592),
                LatLng(-34.364, 147.891),
                LatLng(-33.501, 150.217),
                LatLng(-32.306, 149.248),
                LatLng(-32.491, 147.309)))
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.tag = "A"
        // Style the polyline.
//        stylePolyline(polyline1)

        val polyline2 = mMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(-29.501, 119.700),
                LatLng(-27.456, 119.672),
                LatLng(-25.971, 124.187),
                LatLng(-28.081, 126.555),
                LatLng(-28.848, 124.229),
                LatLng(-28.215, 123.938)))
        polyline2.tag = "B"
//        stylePolyline(polyline2)

        // Add polygons to indicate areas on the map.
        val polygon1 = mMap.addPolygon(PolygonOptions()
            .clickable(true)
            .add(
                LatLng(-27.457, 153.040),
                LatLng(-33.852, 151.211),
                LatLng(-37.813, 144.962),
                LatLng(-34.928, 138.599)))
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.tag = "alpha"
        // Style the polygon.
//        stylePolygon(polygon1)

        val polygon2 = mMap.addPolygon(PolygonOptions()
            .clickable(true)
            .add(
                LatLng(-31.673, 128.892),
                LatLng(-31.952, 115.857),
                LatLng(-17.785, 122.258),
                LatLng(-12.4258, 130.7932)))
        polygon2.tag = "beta"
//        stylePolygon(polygon2)

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
        mMap.setOnPolylineClickListener(this)
        mMap.setOnPolygonClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)

    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true

        requestPermission.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION))
    }


    //custom interface
    override fun locationChanged(location: Location?) {
        Log.e(TAG, "locationChanged: " )
        moveMarkerToCurrentLocation(location)
    }

    private fun addCirclePolygon(location: Location?) {
        val circleOptions = CircleOptions()
            .center(LatLng(location!!.latitude, location.longitude))
            .radius(10000.0) // In meters
            .strokeWidth(10f)
            .strokeColor(Color.RED)
            .fillColor(Color.argb(128, 255, 0, 0))
            .clickable(true)

// Get back the mutable Circle
        val circle = mMap.addCircle(circleOptions)
        mMap.setOnCircleClickListener {

        }
    }
    fun directionPolygon(){
        val polyline1 = mMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(30.3610, 76.8485),
                LatLng(28.7041, 77.1025)))
        polyline1.tag = "A"

        mMap.addPolyline(PolylineOptions())
    }

    private fun moveMarkerToCurrentLocation(location: Location?) {
        val latLng = LatLng(location!!.latitude, location.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
        mMap.animateCamera(cameraUpdate, object : GoogleMap.CancelableCallback {
            @SuppressLint("MissingPermission")
            override fun onFinish() {
    //                mMap.isMyLocationEnabled =false
    //                locationManager.removeUpdates(this@MapsActivity)

                val currentLcoation = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions()
                    .position(currentLcoation))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLcoation))
            }

            override fun onCancel() {}
        })
    }

    private fun bound_area(location: Location) {
        val YOUR_CITY_OR_COUNTRY = LatLngBounds(
            LatLng(location.latitude - 0.1F, location.longitude - 0.1F),
            LatLng(location.latitude + 0.1F, location.longitude + 0.1F))
        mMap.setLatLngBoundsForCameraTarget(YOUR_CITY_OR_COUNTRY)
    }

    @SuppressLint("MissingPermission")
    private fun customIcon_ClickMethod() {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (myLocation == null) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            val provider = lm.getBestProvider(criteria, true)
            myLocation = lm.getLastKnownLocation(provider!!)
        }

        if (myLocation != null) {
            val userLocation = LatLng(myLocation.latitude, myLocation.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f),
                1500, null)
        } else {
            Log.e(TAG, "customIcon_ClickMethod: m location null")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gpsTracker?.let {
            it.stopUsingGPS()
            it.stopSelf()
        }
    }

    fun checkGPS() {
        var intentSenderRequest: IntentSenderRequest? = null
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) {
            Log.e("//", "OnSuccess")
            // GPS is ON
            gpsTracker = GpsTracker(this)
        }
        task.addOnFailureListener(this, OnFailureListener { e ->
            Log.e("//", "GPS off")
            // GPS off
            if (e is ResolvableApiException) {
                try {
                    intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                    getCheckLocationOK.launch(intentSenderRequest)
                    intentSenderRequest = null
                } catch (e1: SendIntentException) {
                    e1.printStackTrace()
                }
            }
        })

    }

    override fun onPolylineClick(p0: Polyline) {

    }

    override fun onPolygonClick(p0: Polygon) {

    }

}