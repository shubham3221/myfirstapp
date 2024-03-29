package com.example.myfirstapp.paging

import com.example.myfirstapp.Myinterceptor
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface PagingService {

    @GET
    suspend fun getUser(@Url url: String): PagingDataClass

    @GET
    suspend fun getUser2(@Url url: String): Call<PagingDataClass>

    @GET
    suspend fun getUser4(@Url url: String): Response<PagingDataClass>

    @GET
    fun getUser3(@Url url: String): Call<PagingDataClass>

    companion object {
        val Baseurl = "https://reqres.in/api/users/1/"

        fun getApiService() = Retrofit.Builder()
            .baseUrl(Baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(Myinterceptor.addInterceptro())
            .build()
            .create(PagingService::class.java)
    }
}
