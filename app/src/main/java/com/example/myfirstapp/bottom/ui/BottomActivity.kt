package com.example.myfirstapp.bottom.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.example.myfirstapp.R
import com.example.myfirstapp.bottom.ui.dashboard.DashboardFragment
import com.example.myfirstapp.bottom.ui.home.HomeFragment
import com.example.myfirstapp.bottom.ui.notifications.NotificationsFragment
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

//Exception function

fun Exception.findJsonObject(e: Exception): JSONObject? {
    if (e is HttpException){
        val response = e.response()
        response?.let {
            it.errorBody()?.let { body->
                return JSONObject(body.charStream().readText())
            }
        }
    }
    return null
}

fun Exception.findErrorMessage(e: Exception): String {
    when (e) {
        is SocketException -> return "Bad Internet"
        is SocketTimeoutException ->return "timeout"
        is HttpException -> return "Http Error"
        is UnknownHostException -> return "Connection Error"
    }
    e.message?.let {
        if (e.message!!.contains("CLEARTEXT")){
            return "Https Required!"
        }
    }
    return "${e.message}"
}

fun Exception.toJSONString(e: Exception): String? {
    when (e) {
        is HttpException -> {
            val response = e.response()
            response?.let {
                it.errorBody()?.let { body->
                    return body.charStream().readText()
                }
            }
        }
    }
    return null
}

enum class Status2 {
    SUCCESS,
    ERROR,
    UNSUCCESSFUL,
}
data class MyResult2<out T>(val status: Status2, val data: T? , val message: String? , val jsonObject: JSONObject?) {

    constructor(status: Status2) : this(status, null, null,null)
    constructor(status: Status2,message: String?) : this(status, null, message,null)
    constructor(status: Status2, data: T?) : this(status, data, null,null)
    constructor(status: Status2, data: T?,message: String?) : this(status, data, message,null)
    constructor(status: Status2, jsonObject: JSONObject?, message: String?) : this(status, null, message,null)

    companion object {
        fun <T> success(data: T?): MyResult2<T> {
            return MyResult2(status = Status2.SUCCESS, data = data)
        }
        fun <T> unsuccess(data: T?): MyResult2<T> {
            return MyResult2(status = Status2.UNSUCCESSFUL, data = data)
        }
        fun <T> unsuccess(data: T?, jsonObject: JSONObject?, message: String?): MyResult2<T>
                = MyResult2(status = Status2.UNSUCCESSFUL,jsonObject = jsonObject, message = message, data = data)



        fun <T> empty(data: T?): MyResult2<T> = MyResult2(status = Status2.ERROR, data = data)
        fun <T> success(data: T? , message: String?): MyResult2<T> = MyResult2(status = Status2.SUCCESS, data = data , message)

        fun <T> error(data: T?, jsonObject: JSONObject?, message: String?): MyResult2<T> = MyResult2(status = Status2.ERROR,jsonObject = jsonObject, message = message)

        fun <T> error(data: T?, message: String?): MyResult2<T> =
            MyResult2(status = Status2.ERROR, data = data, message = message)

    }
}

inline fun <reified T> modelConvert(data: String): T {
    val replace = GsonBuilder().create().fromJson(data, object : TypeToken<T>() {}.type) as T
    return replace
}

inline fun <reified T> mapsModelReturn(data: Any): T {
    val replace = Gson().toJson(data)
    val turnsType = object : TypeToken<T>() {}.type
    val turns = Gson().fromJson<T>(replace, turnsType)
    return turns
}

fun<T> CoroutineScope.safeLaunch(liveData: MutableLiveData<MyResult2<T>>, launchBody: suspend () -> Unit): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, error ->
        val b = (error as Exception).findJsonObject(error)
        if (b!=null){
            liveData.value = MyResult2.unsuccess(null,b,null)
        }else{
            liveData.value = MyResult2.error(null,message= error.findErrorMessage(error))
        }
    }

    return this.launch(coroutineExceptionHandler) {
        launchBody.invoke()
    }
}
fun<T> CoroutineScope.safeLaunchWithCallback(
    callback: (MyResult2<T>) -> Unit,
    launchBody: suspend () -> Unit): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, error ->
        val b = (error as Exception).findJsonObject(error)
        if (b!=null){
            callback(MyResult2.unsuccess(null,b,null))
        }else{
            callback(MyResult2.error(null,message= error.findErrorMessage(error)))
        }
    }

    return this.launch(coroutineExceptionHandler) {
        launchBody.invoke()
    }
}

class BottomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom)

        val fragmentList = arrayListOf(
            DashboardFragment.newInstance(),
            HomeFragment.newInstance(),
            NotificationsFragment.newInstance()
        )
        viewpager2.adapter = ViewPagerAdapter(this, fragmentList)
        viewpager2.offscreenPageLimit=1
        viewpager2.isUserInputEnabled = false
        viewpager2.setPageTransformer(null)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home->viewpager2.currentItem = 0
                R.id.navigation_dashboard->viewpager2.currentItem = 1
                R.id.navigation_notifications->viewpager2.currentItem = 2
            }
            true
        }
    }
}