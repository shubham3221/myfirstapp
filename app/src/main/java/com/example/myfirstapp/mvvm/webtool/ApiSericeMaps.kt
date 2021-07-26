package com.example.myfirstapp.mvvm.webtool

import com.example.example.MapsModel
import com.example.myfirstapp.mvvm.model.Postmodel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceMaps {
    @GET("driving-car?api_key=5b3ce3597851110001cf62483f87c6abec40400e883f337a3e6c91ea")
    suspend fun getPostsWithSuspend(@Query("start") start:String , @Query("end") end:String): MapsModel
}