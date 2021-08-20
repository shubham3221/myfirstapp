package com.example.myfirstapp.paging

import android.util.Log
import androidx.lifecycle.*
import com.example.myfirstapp.MyResult2
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.extentions.makeApiCall
import com.example.myfirstapp.extra.findErrorMessage
import com.example.myfirstapp.extra.findJsonObject
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Response

class PagingViewModel(val apiService: PagingService) :ViewModel() {
    var list = MutableLiveData<MyResult2<PagingDataClass>>()

    fun getPosts(url:String) = liveData(Dispatchers.IO) {
        try {
            emit(MyResult2.success(apiService.getUser(url)))
        }catch (e:Exception){
           emit(MyResult2.error(null,e.findJsonObject(e),e.findErrorMessage(e)))
        }
    }

    fun getPosts10(url:String) = liveData<MyResult2<PagingDataClass>> {
        try {
            makeApiCall(apiService.getUser4(url))
        }catch (e:Exception){
            emit(MyResult2.error(null,e.findErrorMessage(e)))
        }
    }
    fun getPosts2(url:String , callback:(PagingDataClass) -> Unit){
        apiService.getUser3(url).enqueue(object :retrofit2.Callback<PagingDataClass>{
            override fun onResponse(
                call: Call<PagingDataClass>,
                response: Response<PagingDataClass>,
            ) {
                Log.e(TAG, "onResponse: "+response.body()!!.data.email )
                callback(response.body()!!)
            }

            override fun onFailure(call: Call<PagingDataClass>, t: Throwable) {
                Log.e(TAG, "onFailure: "+t.message )
            }

        })
    }
}