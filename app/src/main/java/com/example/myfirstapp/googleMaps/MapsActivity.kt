package com.example.myfirstapp.googleMaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender.SendIntentException
import android.graphics.*
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myfirstapp.googleMaps.autocomplete.SuggestionAdapter
import com.example.myfirstapp.googleMaps.autocomplete.Suggestions
import com.example.myfirstapp.googleMaps.helper.*
import com.example.myfirstapp.googleMaps.helper.MapsHelper.moveCamera
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.Status2
import com.example.myfirstapp.extra.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationChangedListener {
    var mapFragment: SupportMapFragment? = null
    private lateinit var mMap: GoogleMap
    var gpsTracker: GpsTracker? = null
    var where: LatLng? = null
    var toWhere: LatLng? = null
    var allMarkers: ArrayList<Marker> = ArrayList()
    var polygonsArr: ArrayList<LatLng> = ArrayList()
    lateinit var polyline: Polyline

    val getCheckLocationOK: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            when (it.resultCode) {
                RESULT_OK -> {
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

    private val carIcon: BitmapDescriptor by lazy {
        BitmapHelper.vectorToBitmap(this, R.drawable.car, null)
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val pending = it.filterValues { values -> values == false }
            Log.e(TAG, ": " + pending)
            if (pending.isNullOrEmpty()) permissionGiven() else BasicHelper.showDialogPermission(
                this,
                pending.keys.first()) { permissionDenied ->
                if (permissionDenied) {
                    Toast.makeText(applicationContext, "Permission Denied!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
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

        requestPermission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION))
    }


    @SuppressLint("MissingPermission")
    private fun permissionGiven() {
        LocationHelper.registerLifecycleOwner(lifecycle)
        mMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
        checkGPS()
        whereEdittextListener()
        toGoEditedListener()

        mMap.setOnMapClickListener {
            addMarkerWithText(it, true)
        }
    }

    //custom interface
    @SuppressLint("MissingPermission")
    override fun locationChanged(location: Location?) {
        val latLng = LatLng(location!!.latitude, location.longitude)
        Log.e(TAG, "locationChanged1: " + location!!.latitude)
//        toCurrentLocation(mMap, location, liveLocation = true, gpsTracker!!) {
//            addMarker(it, true)
//        }
//        MapsHelper.directionFakePolygon(mMap, addMarker = true, animate = false)

        if (::polyline.isInitialized) {
            MapsHelper.updatePolyline(currentLatlng = latLng, polyline)

            //draw reverse polyline
//            polygonsArr.add(latLng)
//            getSharedPreferences("latlng_move", MODE_PRIVATE)["lat_move"] = polygonsArr
//            MapsHelper.addPolygonFake(polygonsArr, mMap)


//            moveCar(latlng = latLng)
//            moveCamera(latlong = latLng,true,mMap)

            //for moving car and plain
            MapsHelperUber.updateCarLocation(this,latLng,mMap)
        } else {
            mMap.isMyLocationEnabled = false
            mMap.uiSettings.isMyLocationButtonEnabled = true
            addingFakePolyline_SharedPref()
        }
    }


    private fun addingFakePolyline_SharedPref() {

        getSharedPreferences("latlng", MODE_PRIVATE).getObject<List<List<Double>>>("values")?.let {

            polyline = MapsHelper.drawPolygonApi(it, mMap)
//            for (i in it) {
//                mapCordinates!!.add(i.toDoubleArray())
//            }
            where = LatLng(it[0][1], it[0][0])
            toWhere = LatLng(it[it.size - 1][1], it[it.size - 1][0])

            moveCamera(where!!, toWhere!!, true, mMap, this)

//            allMarkers.add(MapsHelper.addMarker(mMap,
//                where!!,
//                false,
//                icon = carIcon,
//                bearing = true,
//                toWhere!!)!!)
//            allMarkers.add(MapsHelper.addMarker(mMap, toWhere!!, false))


            MapsHelperUber.addBlackMarker(where!!,toWhere!!,mMap)

//            val polylineAnimator = MapsHelper.polylineAnimator()
//            polylineAnimator.addUpdateListener { valueAnimator ->
//                val percentValue = (valueAnimator.animatedValue as Int)
//                val index = (polyline.points.size) * (percentValue / 100.0f).toInt()
//                polyline.points = polyline.points.subList(0, index)
//            }
//            polylineAnimator.start()
        }
    }

    private fun moveCar(latlng: LatLng) {
        allMarkers[0].rotation = MapsHelper.calculateBearing(lat1 = latlng.latitude,
            latlng.longitude,
            toWhere!!.latitude,
            toWhere!!.longitude)
        allMarkers[0].position = latlng
//        where = LatLng(mapCordinates!![0][1], mapCordinates!![0][0])
//        toWhere = LatLng(mapCordinates!![mapCordinates!!.size - 1][1],
//            mapCordinates!![mapCordinates!!.size - 1][0])

    }
//

    private fun addMarkerWithText(it: LatLng, changeIcon: Boolean) {
        LocationHelper.getAddress(this@MapsActivity, it) { address ->
            address.apply {
                if (!changeIcon) MapsHelper.addMarker(mMap = mMap,
                    latlong = it,
                    animate = true).tag = Place(city, it, address.address, 4F)
                else MapsHelper.addMarker(mMap = mMap,
                    latlong = it,
                    animate = true,
                    icon = bicycleIcon).tag = Place(city, it, address.address, 4F)
                location_TextView.text =
                    "${address.address} $city $country $knownName $postalCode $state"
            }
        }
    }

    fun whereEdittextListener() {
        val mAdapter = SuggestionAdapter(this, R.layout.layout_item_places, ArrayList())
        place.setAdapter(mAdapter)
        place.addTextChangedListener(Suggestions(lifecycle, mAdapter))

        place.onAction(EditorInfo.IME_ACTION_DONE) { textView ->
            LocationHelper.getLatlngFromAddress(this, textView.text.toString()) {
                it?.let {
                    where = it
                    mMap.clear()
                    addMarkerWithText(where!!, false)
                } ?: Toast.makeText(applicationContext, "Not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toGoEditedListener() {
        val mAdapter = SuggestionAdapter(this, R.layout.layout_item_places, ArrayList())
        placeToGo.setAdapter(mAdapter)
        placeToGo.addTextChangedListener(Suggestions(lifecycle, mAdapter))


        placeToGo.onAction(EditorInfo.IME_ACTION_DONE) {
            LocationHelper.getLatlngFromAddress(this, it.text.toString()) { latlng ->
                latlng?.let {
                    toWhere = latlng
                    changeText("waiting for api response...")
                    MapsHelper.addMarker(mMap, latlng, false)
                    apiCallForDirection()
                } ?: Toast.makeText(applicationContext, "Not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun apiCallForDirection() {
        MapApiHelper.getDirectionsApi("${where!!.longitude},${where!!.latitude}",
            "${toWhere!!.longitude} , ${toWhere!!.latitude}")
            .observe(this@MapsActivity, {
                when (it.status) {
                    Status2.SUCCESS -> {
                        changeText("Success..")
                        val mapsModel = it.data!!.features[0].geometry.coordinates
                        getSharedPreferences("latlng", MODE_PRIVATE)["values"] = mapsModel
                        MapsHelper.drawPolygonApi(mapsModel, mMap)
                        moveCamera(where!!, toWhere!!, true, mMap, this)
                    }
                    Status2.ERROR -> {
                        it.jsonObject?.let { jsonObj ->
                            changeText("Error Message : ${it.message} Response : ${
                                jsonObj.getJSONObject("error").getString("message")
                            }")
                        } ?: changeText("Error: " + it.message)
                    }
                }
            })
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
            gpsTracker?.setLocationCallback(this)
        }
        task.addOnFailureListener(this) { e ->
            Log.e("//", "GPS off")
            // GPS off
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    try {
                        e as ResolvableApiException
                        intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                        getCheckLocationOK.launch(intentSenderRequest)
                        intentSenderRequest = null
                    } catch (e1: SendIntentException) {
                        e1.printStackTrace()
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(applicationContext, "Turn off airplain mode", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    }

    fun changeText(text: String) {
        location_TextView.text = text
    }
}