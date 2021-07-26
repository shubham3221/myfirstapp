package com.example.myfirstapp.GoogleMaps.helper

import androidx.lifecycle.liveData
import com.example.myfirstapp.Modelclass
import com.example.myfirstapp.MyResult2
import com.example.myfirstapp.mvvm.webtool.RetrofitBuilder
import com.example.myfirstapp.extra.toJSONObject
import com.example.myfirstapp.extra.toJSONString
import com.example.myfirstapp.extra.toObject
import kotlinx.coroutines.Dispatchers
import java.lang.Exception


object MapApiHelper {
    val api = RetrofitBuilder.apiService_GoogleMaps

    fun getDirectionsApi(start: String, end: String) = liveData(Dispatchers.IO) {
        try {
            emit(MyResult2.success(api.getPostsWithSuspend(start, end)))
        }catch (e: Exception){
            emit(MyResult2.error(null,e.toJSONObject(e)!!, e.message!!))
        }
    }
}