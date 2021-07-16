package com.example.myfirstapp.GoogleMaps

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class PlaceResponse(
    val geometry: Geometry,
    val name: String,
    val vicinity: String,
    val rating: Float
) {

    data class Geometry(
        val location: GeometryLocation
    )

    data class GeometryLocation(
        val lat: Double,
        val lng: Double
    )
}

fun PlaceResponse.toPlace(): Place = Place(
    name = name,
    latLng = LatLng(geometry.location.lat, geometry.location.lng),
    address = vicinity,
    rating = rating
)

data class Place(
    val name: String,
    val latLng: LatLng,
    val address: String,
    val rating: Float
) : ClusterItem {
    override fun getPosition(): LatLng =
        latLng

    override fun getTitle(): String =
        name

    override fun getSnippet(): String =
        address
}