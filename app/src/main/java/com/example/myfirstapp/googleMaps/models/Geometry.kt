package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Geometry (

   @SerializedName("coordinates") var coordinates : List<List<Double>>,
   @SerializedName("type") var type : String

)