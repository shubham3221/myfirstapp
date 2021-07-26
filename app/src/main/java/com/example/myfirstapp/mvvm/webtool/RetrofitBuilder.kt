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




object RetrofitBuilder {

    const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    const val END_POINT = "posts"
    private const val UPLOAD_URL = "https://api.imgur.com/"
    private const val IMGUR_TOKEN = "d76a7153e983213940780cbcc6f36621ec29b2aa"
    private const val DIRECTIONS = "https://api.openrouteservice.org/v2/directions/"

    private fun getRetrofit(url:String): Retrofit {
        return Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(Myinterceptor.addInterceptro())
            .build()
    }

    val apiService_JsonPlaceholder: ApiService = getRetrofit(BASE_URL).create(ApiService::class.java)
    val apiService_Imagur: ApiService = getRetrofit(UPLOAD_URL).create(ApiService::class.java)
    val apiService_GoogleMaps: ApiServiceMaps = getRetrofit(DIRECTIONS).create(ApiServiceMaps::class.java)
}

//class RetrofitBuilder {
//
//    object jsonPlaceHolder {
//        private fun getRetrofit(): Retrofit {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//        }
//
//        val apiService: ApiService = getRetrofit().create(ApiService::class.java)
//
//
//        val builder =
//            Retrofit.Builder().baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
//            .client(Myinterceptor.addIntercepterImgur())
//            .build()
//        val retrofit = builder.create(ApiService::class.java)
//    }
//
//    object uploadImage {
//        val builder = Retrofit.Builder().baseUrl(UPLOAD_URL)
//            .addConverterFactory(GsonConverterFactory.create(
//                GsonBuilder().setLenient().create()))
//            .client(Myinterceptor.addIntercepterImgur())
//            .build()
//        val retrofit = builder.create(ApiService::class.java)
//    }
//
//}