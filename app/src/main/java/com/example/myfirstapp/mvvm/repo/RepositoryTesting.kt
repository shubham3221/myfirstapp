package com.example.myfirstapp.mvvm.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.mvvm.model.Postmodel
import com.example.myfirstapp.mvvm.viewmodel.MyResult
import com.example.myfirstapp.mvvm.webtool.RetrofitBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RepositoryTesting {
    var isFirsttime = true
    var liveData: MutableLiveData<ArrayList<Mymodel>> = MutableLiveData<ArrayList<Mymodel>>()

    //retrofit
    var postList: MutableLiveData<ArrayList<Postmodel>> = MutableLiveData<ArrayList<Postmodel>>()
    var status: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var specificpostList: MutableLiveData<ArrayList<Postmodel>> = MutableLiveData<ArrayList<Postmodel>>()

    fun getImages(): MutableLiveData<ArrayList<Mymodel>> {
        liveData.value = ArrayList<Mymodel>().apply {
            this.add(Mymodel(R.drawable.facebook))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.facebook))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.facebook))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
            this.add(Mymodel(R.drawable.apple))
        }
        return liveData
    }

    fun getData2(): MutableLiveData<ArrayList<Mymodel>> {
        liveData.value = ArrayList<Mymodel>().apply {

            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0..5) {
                    delay(1000)
                    Log.e(TAG, "image added: " + i)
                    this@apply.add(Mymodel(R.drawable.facebook))
                    withContext(Dispatchers.Main) {
                        liveData.value = this@apply
                    }
                }
            }
        }
        return liveData
    }

    fun addingImage(number: Int): MutableLiveData<ArrayList<Mymodel>> {
        //checking if value is not null then add into same
        liveData.value?.apply {
            for (i in 0..number) {
                this.add(Mymodel(R.drawable.apple))
            }
            liveData.postValue(this)
        } ?: run {
            liveData.value = ArrayList<Mymodel>().apply {
                for (i in 0..number) {
                    this.add(Mymodel(R.drawable.apple))
                }
                liveData.postValue(this)
            }
        }
        return liveData
    }

    fun fetchSpecificPosts(num: Int, mdata: MutableLiveData<ArrayList<Postmodel>>) {
        status.value = true
        RetrofitBuilder.apiService_JsonPlaceholder.getPosts()
            .enqueue(object : Callback<ArrayList<Postmodel>> {

                override fun onResponse(
                    call: Call<ArrayList<Postmodel>>,
                    response: Response<ArrayList<Postmodel>>,
                ) {
                    status.value = false
                    Log.e(TAG, "onResponse: ")
                    if (isFirsttime) {
                        isFirsttime = false
                        mdata.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<ArrayList<Postmodel>>, t: Throwable) {
                    Log.e(TAG, "onFailure: " + t.message)
                }
            })
    }


    suspend fun getPosts() = RetrofitBuilder.apiService_JsonPlaceholder.getPostsWithSuspend()
    suspend fun getSpecificPosts(num: Int) = RetrofitBuilder.apiService_JsonPlaceholder.getSpecific_WithSuspend(num)
    suspend fun postRequest() = RetrofitBuilder.apiService_JsonPlaceholder.postRequest()
    fun uploadImage(image:MultipartBody.Part) = RetrofitBuilder.apiService_Imagur.uploadImage(image)


    fun getPost(list: MutableLiveData<ArrayList<Postmodel>>) = liveData(Dispatchers.IO) {
        emit(MyResult.loading(data = null))
        try {
            emit(MyResult.success(getPosts()))
        } catch (e: Exception) {
            emit(MyResult.error(data = null, message = e.message ?: "Error Occurred"))
        }
    }

    fun getSpecificPost(num: Int) = liveData(Dispatchers.IO) {
        emit(MyResult.loading(data = null))
        try {
            emit(MyResult.success(getSpecificPosts(num)).also {
                specificpostList.postValue(it.data)
            })
        } catch (e: Exception) {
            emit(MyResult.error(data = null, message = e.message ?: "Error Occurred"))
        }
    }


    fun fetch_PostRequest() = liveData(Dispatchers.IO) {
        emit(MyResult.loading(null))
        try {
            emit(MyResult.success(postRequest()))
        } catch (e: Exception) {
            emit(MyResult.error(null, e.message ?: "Error occured"))
        }
    }


    fun uploadImgaeToServer(image: MultipartBody.Part) = liveData(Dispatchers.IO) {
        emit(MyResult.loading(null))
        try {
            emit(MyResult.success(uploadImage(image)))
        } catch (e:Exception){
            emit(MyResult.error(null,e.message.toString()))
        }
    }

}