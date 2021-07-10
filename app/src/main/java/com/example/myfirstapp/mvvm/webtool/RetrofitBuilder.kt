package com.example.myfirstapp.mvvm.webtool

import com.example.myfirstapp.Myinterceptor
import com.example.myfirstapp.mvvm.model.Postmodel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val BASE_URL = "https://jsonplaceholder.typicode.com/"
const val END_POINT = "posts"
private const val UPLOAD_URL = "https://api.imgur.com/"
private const val IMGUR_TOKEN = "d76a7153e983213940780cbcc6f36621ec29b2aa"


class RetrofitBuilder {

    interface ApiService {
        @GET(END_POINT)
        fun getPosts(): Call<ArrayList<Postmodel>>

        @GET(END_POINT)
        suspend fun getPostsWithSuspend(): ArrayList<Postmodel>

        @GET(END_POINT)
        suspend fun getSpecific_WithSuspend(@Query("id") id: Int): ArrayList<Postmodel>

        @POST("/posts")
        suspend fun postRequest():JsonObject


        @Multipart
        @POST("3/image")
         suspend fun uploadImage(@Part() image: MultipartBody.Part?) : JsonObject

    }

    object jsonPlaceHolder {
        val builder = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().setLenient().create()))
            .client(Myinterceptor.addIntercepterImgur())
            .build()
        val retrofit = builder.create(ApiService::class.java)
    }
    object uploadImage{
        val builder = Retrofit.Builder().baseUrl(UPLOAD_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().setLenient().create()))
            .client(Myinterceptor.addIntercepterImgur())
            .build()
        val retrofit = builder.create(ApiService::class.java)
    }

}