package com.example.example

import com.google.gson.annotations.SerializedName

   
data class MapsModel (

   @SerializedName("type") var type : String,
   @SerializedName("features") var features : List<Features>,
   @SerializedName("bbox") var bbox : List<Double>,
   @SerializedName("metadata") var metadata : Metadata

)