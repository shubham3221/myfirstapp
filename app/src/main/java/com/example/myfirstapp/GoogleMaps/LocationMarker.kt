package com.example.myfirstapp.GoogleMaps

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import java.util.logging.Handler


object LocationMarker:LifecycleOwner,LifecycleObserver {

    private lateinit var scope: LifecycleCoroutineScope

    //Inside custom view
    fun registerLifecycleOwner(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }

    val applicationScope = CoroutineScope(Job() + Dispatchers.IO)
    private val registry = LifecycleRegistry(this)
    data class MyAddress(
        val address: String,
        val city: String,
        val state: String?,
        val country: String,
        val postalCode: String?,
        val knownName: String,
    )

    fun showSingleLocation(
        mMap: GoogleMap,
        title: String,
        currentLat: Double?,
        currentLong: Double?,
    ){
        // Add a marker in Sydney and move the camera
        if (currentLat!=null){
            val sydney = LatLng(currentLat, currentLong!!)
            mMap.addMarker(MarkerOptions()
                .position(sydney)

                .title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }else{
            val sydney = LatLng(-34.0, 151.0)
            mMap.addMarker(MarkerOptions()
                .position(sydney)
                .title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

    }

    fun addMarkers_To_Many_Locations(
        googleMap: GoogleMap,
        places: List<Place>,
        bicycleIcon: BitmapDescriptor,
    ) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    .icon(bicycleIcon)

            )
            marker.tag = place
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(places[0].latLng))
    }

    fun addMarker_Single_Locations(
        googleMap: GoogleMap,
        places: List<Place>,
        bicycleIcon: BitmapDescriptor,
    ) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    .icon(bicycleIcon)

            )
            marker.tag = place
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(places[0].latLng))
    }

    fun getAddress(context: Context, latLng: LatLng, callback: (MyAddress) -> Unit) {
        scope.launch {
            for (i in 0..100){
                delay(500)
                Log.e(TAG, "getAddress: "+i )
            }
        }
        lifecycleScope.launch(Dispatchers.IO){


            val df = DecimalFormat()
            df.maximumFractionDigits = 3

            val lat: Double = df.format(latLng.latitude).toDouble()
            val lon: Double = df.format(latLng.longitude).toDouble()

            val addresses: List<Address>?
            Geocoder(context, Locale.getDefault()).also {
                try {
                    addresses = it.getFromLocation(lat, lon, 1)
                }catch (e: IOException){
                    Log.e(TAG, "getAddress: exception: " + e.message)
                    return@launch
                }
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                var city = ""
                addresses[0].locality?.let { city = it } ?: run { city = addresses[0].getAddressLine(
                    0) }
                var state = ""
                addresses[0].adminArea?.let {
                    state = it
                }
                val country = addresses[0].countryName
                var postalCode = ""
                addresses[0].postalCode?.let { postalCode = it }
                val knownName = addresses[0].featureName
                val myAddress = MyAddress(address, city, state, country, postalCode, knownName)
                withContext(Dispatchers.Main){
                    callback(myAddress)
                }
            }
        }
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }
}
object LocationMarkerWithDestroy:LifecycleObserver {

    private lateinit var scope: LifecycleCoroutineScope

    //Inside custom view
    fun registerLifecycleOwner(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }

    data class MyAddress(
        val address: String,
        val city: String,
        val state: String?,
        val country: String,
        val postalCode: String?,
        val knownName: String,
    )

    fun showSingleLocation(
        mMap: GoogleMap,
        title: String,
        currentLat: Double?,
        currentLong: Double?,
    ){
        // Add a marker in Sydney and move the camera
        if (currentLat!=null){
            val sydney = LatLng(currentLat, currentLong!!)
            mMap.addMarker(MarkerOptions()
                .position(sydney)

                .title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }else{
            val sydney = LatLng(-34.0, 151.0)
            mMap.addMarker(MarkerOptions()
                .position(sydney)
                .title(title))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

    }

    fun addMarkers_To_Many_Locations(
        googleMap: GoogleMap,
        places: List<Place>,
        bicycleIcon: BitmapDescriptor,
    ) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    .icon(bicycleIcon)

            )
            marker.tag = place
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(places[0].latLng))
    }

    fun addMarker_Single_Locations(
        googleMap: GoogleMap,
        places: List<Place>,
        bicycleIcon: BitmapDescriptor,
    ) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    .icon(bicycleIcon)

            )
            marker.tag = place
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(places[0].latLng))
    }

    fun getAddress(context: Context, latLng: LatLng, callback: (MyAddress) -> Unit) {

        scope.launch(Dispatchers.IO){
            val df = DecimalFormat()
            df.maximumFractionDigits = 3

            val lat: Double = df.format(latLng.latitude).toDouble()
            val lon: Double = df.format(latLng.longitude).toDouble()

            val addresses: List<Address>?
            Geocoder(context, Locale.getDefault()).also {
                try {
                    addresses = it.getFromLocation(lat, lon, 1)
                }catch (e: IOException){
                    Log.e(TAG, "getAddress: exception: " + e.message)
                    return@launch
                }
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                var city = ""
                addresses[0].locality?.let { city = it } ?: run { city = addresses[0].getAddressLine(
                    0) }
                var state = ""
                addresses[0].adminArea?.let {
                    state = it
                }
                val country = addresses[0].countryName
                var postalCode = ""
                addresses[0].postalCode?.let { postalCode = it }
                val knownName = addresses[0].featureName
                val myAddress = MyAddress(address, city, state, country, postalCode, knownName)
                withContext(Dispatchers.Main){
                    callback(myAddress)
                }
            }
        }
    }
}