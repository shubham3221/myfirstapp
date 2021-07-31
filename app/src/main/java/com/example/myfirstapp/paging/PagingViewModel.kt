package com.example.myfirstapp.paging

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Myconstants.Companion.TAG
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse

class PagingViewModel(val apiService: PagingService) :ViewModel() {

    fun getPosts(url:String) = liveData(Dispatchers.IO) {
        try {
            emit(apiService.getUser(url))
        }catch (e:Exception){
            Log.d(TAG, "emit Exception: "+e.message )
        }
    }
    fun getPosts2(url:String) {
        viewModelScope.makeApiCall(suspend { apiService.getUser2(url).execute() }, {
            Log.e(TAG, "getPosts2: "+it.message )
        }){ data->
            Log.e(TAG, "getPosts2: "+data?.data?.email)
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