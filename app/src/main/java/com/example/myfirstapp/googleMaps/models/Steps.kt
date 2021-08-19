package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Steps (

   @SerializedName("distance") var distance : Double,
   @SerializedName("duration") var duration : Double,
   @SerializedName("type") var type : Int,
   @SerializedName("instruction") var instruction : String,
   @SerializedName("name") var name : String,
   @SerializedName("way_points") var wayPoints : List<String>

)