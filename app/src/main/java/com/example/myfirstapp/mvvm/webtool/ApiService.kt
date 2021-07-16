package com.example.myfirstapp.mvvm.webtool

import com.example.myfirstapp.mvvm.model.Postmodel
import com.example.myfirstapp.mvvm.webtool.RetrofitBuilder.END_POINT
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
interface ApiService {
    @GET(END_POINT)
    fun getPosts(): Call<ArrayList<Postmodel>>

    @GET(END_POINT)
    suspend fun getPostsWithSuspend(): ArrayList<Postmodel>

    @GET(END_POINT)
    suspend fun getSpecific_WithSuspend(@Query("id") id: Int): ArrayList<Postmodel>

    @POST("/posts")
    suspend fun postRequest(): JsonObject

    @Headers("Authorization: Bearer d76a7153e983213940780cbcc6f36621ec29b2aa")
    @Multipart
    @POST("3/image")
    fun uploadImage(@Part() image: MultipartBody.Part?) : JsonObject
}