package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Metadata (

   @SerializedName("attribution") var attribution : String,
   @SerializedName("service") var service : String,
   @SerializedName("timestamp") var timestamp : String,
   @SerializedName("query") var query : Query,
   @SerializedName("engine") var engine : Engine

)