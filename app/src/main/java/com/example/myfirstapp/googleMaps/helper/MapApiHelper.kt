package com.example.myfirstapp.googleMaps.helper

import androidx.lifecycle.liveData
import com.example.myfirstapp.MyResult2
import com.example.myfirstapp.mvvm.webtool.RetrofitBuilder
import com.example.myfirstapp.extra.findJsonObject
import kotlinx.coroutines.Dispatchers
import java.lang.Exception


object MapApiHelper {
    val api = RetrofitBuilder.apiService_GoogleMaps

    fun getDirectionsApi(start: String, end: String) = liveData(Dispatchers.IO) {
        try {
            emit(MyResult2.success(api.getPostsWithSuspend(start, end)))
        }catch (e: Exception){
            emit(MyResult2.error(null,e.findJsonObject(e)!!, e.message!!))
        }
    }
}