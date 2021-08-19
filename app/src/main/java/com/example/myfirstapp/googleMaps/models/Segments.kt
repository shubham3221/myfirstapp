package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Segments (

   @SerializedName("distance") var distance : Double,
   @SerializedName("duration") var duration : Double,
   @SerializedName("steps") var steps : List<Steps>

)