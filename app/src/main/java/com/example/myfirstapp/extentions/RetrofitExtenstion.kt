package com.example.myfirstapp.extentions

import androidx.lifecycle.LiveDataScope
import com.example.myfirstapp.MyResult2
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

//fun <T> ViewModel.makeApiCallLiveData(apiCall: suspend () -> Response<T>): LiveData<RetrofitResult<T>> {
//    return liveData(context = viewModelScope.coroutineContext) {
//        emit(RetrofitResult.Loading)
//        try {
//            subscribeApiCall(apiCall.invoke())
//        } catch (t: Throwable) {
//            emit(RetrofitResult.Error(t))
//        }
//    }
//}

suspend fun <T> LiveDataScope<MyResult2<T>>.makeApiCall(res: Response<T>) {
    if (res.isSuccessful) {
        val body = res.body()
        if (body == null) {
            emit(MyResult2.success(null))
        } else {
            if (body is List<*>) {
                val list = body as List<*>
                if (list.isNullOrEmpty()) {
                    emit(MyResult2.success(null))
                } else {
                    emit(MyResult2.success(body))
                }
            } else {
                emit(MyResult2.success(body))
            }
        }
    } else {
        emit(MyResult2.unsuccess(null,JSONObject(res.errorBody()?.charStream()?.readText()!!) , null))
    }
}

fun <T> CoroutineScope.makeApiCallAsync(apiCall: suspend () -> Response<T>,
                                        onError: (throwable: Throwable) -> Unit = { _ -> },
                                        onUnsuccessfulCall: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> },
                                        onResponse: (response: T?) -> Unit
): Job {

    return launch(Dispatchers.Default) {
        supervisorScope {
            try {
                val task = async(Dispatchers.IO) {
                    apiCall()
                }
                val response = task.await()
                response?.apply {
                    if (isSuccessful) {
                        onResponse(body())
                    } else {
                        onUnsuccessfulCall(errorBody(), code())
                    }
                }
            } catch (t: Throwable) {
                onError(t)
            }
        }

    }
}