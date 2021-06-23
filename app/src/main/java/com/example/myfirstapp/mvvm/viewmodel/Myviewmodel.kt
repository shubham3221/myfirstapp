package com.example.myfirstapp.mvvm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.mvvm.model.Postmodel
import com.example.myfirstapp.mvvm.repo.Repository

class Myviewmodel(application: Application) : AndroidViewModel(application) {
    var repo = Repository()


    fun getData(): MutableLiveData<ArrayList<Mymodel>> {
        return repo.getData()
    }

    fun addImages(num:Int):MutableLiveData<ArrayList<Mymodel>>{
        return repo.addingImage(num)
    }




    var liveList:MutableLiveData<ArrayList<Postmodel>> = MutableLiveData<ArrayList<Postmodel>>()
    fun getStatus() : MutableLiveData<Boolean>{
        return repo.status
    }

    fun getPosts(num: Int){
        liveList.value?.let {
            Log.e(TAG, "getPosts: not null" )
            liveList.postValue(it)
        } ?: kotlin.run {
            Log.e(TAG, "getPosts:  null" )
            repo.fetchPosts(num,liveList)
        }
    }
}