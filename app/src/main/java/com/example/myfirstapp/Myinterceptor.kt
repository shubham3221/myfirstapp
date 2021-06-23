package com.example.myfirstapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object Myinterceptor {

    fun addInterceptro(): OkHttpClient{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            // Request customization: add request headers
//            val requestBuilder = original.newBuilder()
//                .header("Authorization", "MY_API_KEY") // <-- this is the important line

//            val request = requestBuilder.build()
            chain.proceed(original)
        }
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.readTimeout(30, TimeUnit.SECONDS)

        httpClient.addNetworkInterceptor(logging)

        return httpClient.build()
    }

}