package com.example.myfirstapp.paging

import com.google.gson.annotations.SerializedName

data class PagingDataClass(val data:SubData)

data class SubData(val id:Int, @SerializedName("email") val email:String, @SerializedName("first_name") val name:String )