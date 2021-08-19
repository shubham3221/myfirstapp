package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Properties (

   @SerializedName("segments") var segments : List<Segments>,
   @SerializedName("summary") var summary : Summary,
   @SerializedName("way_points") var wayPoints : List<Int>

)