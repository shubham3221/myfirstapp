package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Engine (

   @SerializedName("version") var version : String,
   @SerializedName("build_date") var buildDate : String,
   @SerializedName("graph_date") var graphDate : String

)