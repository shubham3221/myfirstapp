package com.example.myfirstapp.mvvm.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.mvvm.model.Postmodel
import com.example.myfirstapp.mvvm.webtool.Webservice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Repository() {
    var isFirsttime = true
    var liveData: MutableLiveData<ArrayList<Mymodel>> = MutableLiveData<ArrayList<Mymodel>>()

    //retrofit
//    var postList: MutableLiveData<ArrayList<Postmodel>> = MutableLiveData<ArrayList<Postmodel>>()
    var status: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun getData(): MutableLiveData<ArrayList<Mymodel>> {
        liveData.value = ArrayList<Mymodel>().apply {
            this.add(Mymodel(R.drawable.facebook))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.facebook))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.facebook))
            this.add(Mymodel(R.drawable.apple))
        }
        return liveData
    }

    fun addingImage(number:Int):MutableLiveData<ArrayList<Mymodel>>{
        Log.e(TAG, "start: " )
        liveData.value?.apply {
            Log.e(TAG, "in"+ liveData.value!!.size )
            for (i in 0..number){
                Log.e(TAG, "first loop: " )
                this.add(Mymodel(R.drawable.apple))
            }
            liveData.postValue(this)
        } ?: run {
            Log.e(TAG, "out: ")
            liveData.value = ArrayList<Mymodel>().apply {
                for (i in 0..number) {
                    Log.e(TAG, "second loop: ")
                    this.add(Mymodel(R.drawable.apple))
                }
            }
        }
        Log.e(TAG, "exit: " )
        return liveData
    }


    fun fetchPosts(num: Int, mdata: MutableLiveData<ArrayList<Postmodel>>){
        status.value = true
        Webservice.instance.retrofit.getPosts().enqueue(object : Callback<ArrayList<Postmodel>> {
            override fun onResponse(
                call: Call<ArrayList<Postmodel>>,
                response: Response<ArrayList<Postmodel>>
            ) {
                status.value = false
                Log.e(TAG, "onResponse: " )

                if (isFirsttime){
                    isFirsttime = false
                    mdata.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<Postmodel>>, t: Throwable) {
                Log.e(TAG, "onFailure: " + t.message)
            }

        })
    }
}