package com.example.myfirstapp.mvvm.webtool

import com.example.myfirstapp.Myinterceptor
import com.example.myfirstapp.mvvm.model.Postmodel
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://jsonplaceholder.typicode.com/"
const val END_POINT = "posts"


class Webservice {

    interface Callingfunctions {

        @GET(END_POINT)
        fun getPosts(): Call<ArrayList<Postmodel>>
    }

    object instance {
        val builder =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().setLenient().create()))
                .build()
        val retrofit = builder.create(Callingfunctions::class.java)
    }
}