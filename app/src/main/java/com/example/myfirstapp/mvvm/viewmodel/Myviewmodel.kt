package com.example.myfirstapp.mvvm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.model.Mymodel
import com.example.myfirstapp.mvvm.model.Postmodel
import com.example.myfirstapp.mvvm.repo.RepositoryTesting
import okhttp3.MultipartBody

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class MyResult<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): MyResult<T> =
            MyResult(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): MyResult<T> =
            MyResult(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): MyResult<T> =
            MyResult(status = Status.LOADING, data = data, message = null)
    }
}

class Myviewmodel(application: Application) : AndroidViewModel(application) {
    var repositoryTesting = RepositoryTesting()
    var liveData: MutableLiveData<ArrayList<Mymodel>> = MutableLiveData<ArrayList<Mymodel>>()
    var liveList:MutableLiveData<ArrayList<Postmodel>> = MutableLiveData<ArrayList<Postmodel>>()


    fun fetchSpecificPost(num:Int) = repositoryTesting.getSpecificPost(num)
    fun postRequest() = repositoryTesting.fetch_PostRequest()
    fun uploadImage(image:MultipartBody.Part) = repositoryTesting.uploadImgaeToServer(image)


//    fun getData(): MutableLiveData<ArrayList<Mymodel>> {
//        return repo.getData()
//    }
//    fun getData2(): MutableLiveData<ArrayList<Mymodel>> {
//        return repo.getData2()
//    }
//    fun getData2(){
//        liveData.value = ArrayList<Mymodel>().apply {
//            CoroutineScope(Dispatchers.IO).launch {
//                for (i in 0..5){
//                    delay(1000)
//                    Log.e(TAG, "image added: "+i )
//                    this@apply.add(Mymodel(R.drawable.facebook))
//                    withContext(Dispatchers.Main){
//                        liveData.value = this@apply
//                    }
//                }
//            }
//        }
//    }

    fun addImages(num:Int):MutableLiveData<ArrayList<Mymodel>>{
        return repositoryTesting.addingImage(num)
    }

    fun getStatus() : MutableLiveData<Boolean>{
        return repositoryTesting.status
    }

    fun getPosts(num: Int){
        liveList.value?.let {
            Log.e(TAG, "getPosts: not null" )
            liveList.postValue(it)
        } ?: kotlin.run {
            Log.e(TAG, "getPosts:  null" )
            repositoryTesting.fetchSpecificPosts(num,liveList)
        }
    }
}