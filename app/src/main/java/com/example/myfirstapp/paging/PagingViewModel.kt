package com.example.myfirstapp.paging

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.myfirstapp.Myconstants.Companion.TAG
import kotlinx.coroutines.Dispatchers

class PagingViewModel(val apiService: PagingService) :ViewModel() {
    val repo = PagingRepo()

    fun getPosts(url:String) = liveData(Dispatchers.IO) {
        try {
            emit(apiService.getUser(url))
        }catch (e:Exception){
            Log.e(TAG, "getPosts: "+e.message )
        }
    }



}