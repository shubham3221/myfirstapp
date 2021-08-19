package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Features (

   @SerializedName("bbox") var bbox : List<Double>,
   @SerializedName("type") var type : String,
   @SerializedName("properties") var properties : Properties,
   @SerializedName("geometry") var geometry : Geometry

)