package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Query (

   @SerializedName("coordinates") var coordinates : List<List<Double>>,
   @SerializedName("profile") var profile : String,
   @SerializedName("format") var format : String

)