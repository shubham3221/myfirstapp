package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Summary (

   @SerializedName("distance") var distance : Double,
   @SerializedName("duration") var duration : Double

)