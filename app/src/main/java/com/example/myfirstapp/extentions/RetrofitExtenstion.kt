package com.example.myfirstapp.extentions

import androidx.lifecycle.LiveDataScope
import com.example.myfirstapp.MyResult2
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

sealed class RetrofitResult<out T> {

    data class Success<T>(val value: T) : RetrofitResult<T>() {
        val isValueAListAndNullOrEmpty get() = value is List<*> && value.isNullOrEmpty()
    } // handle UI changes when everything is loaded

    object Loading : RetrofitResult<Nothing>() // handle loading state
    object EmptyData : RetrofitResult<Nothing>() //same as no data except this one returns that no data was obtained from the server
    data class Error(val throwable: Throwable) : RetrofitResult<Nothing>() //this one gets thrown when there's an error on your side
    data class ApiError(val responseCode: Int, val errorBody: ResponseBody?) : RetrofitResult<Nothing>() //whenever the api throws an error

}

typealias RetrofitState<T> = MutableStateFlow<RetrofitResult<T>>

fun <T> retrofitStateInitialLoading(): RetrofitState<T> = MutableStateFlow(RetrofitResult.Loading)
fun <T> retrofitStateInitialEmptyData(): RetrofitState<T> = MutableStateFlow(RetrofitResult.EmptyData)
fun <T> retrofitStateInitialSuccess(value: T): RetrofitState<T> = MutableStateFlow(RetrofitResult.Success(value))

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
suspend fun <T> LiveDataScope<RetrofitResult<T>>.subscribeApiCallList(res: Response<T>) {

    if (res.isSuccessful) {
        val body = res.body()
        if (body == null) {
            emit(RetrofitResult.EmptyData)
        } else {
            if (body is List<*>) {
                val list = body as List<*>
                if (list.isNullOrEmpty()) {
                    emit(RetrofitResult.EmptyData)
                } else {
                    emit(RetrofitResult.Success(body))
                }
            } else {
                emit(RetrofitResult.Success(body))
            }
        }
    } else {
        emit(RetrofitResult.ApiError(res.code(), res.errorBody()))
    }
}

suspend fun <T> LiveDataScope<MyResult2<T>>.makeApiCall(res: Response<T>) {
    if (res.isSuccessful) {
        val body = res.body()
        if (body == null) {
            emit(MyResult2.error(null,""))
        } else {
            if (body is List<*>) {
                val list = body as List<*>
                if (list.isNullOrEmpty()) {
                    emit(MyResult2.empty(null))
                } else {
                    emit(MyResult2.success(body))
                }
            } else {
                emit(MyResult2.success(body))
            }
        }
    } else {
        emit(MyResult2.error(null, JSONObject(res.errorBody()?.charStream()?.readText()!!), ""))
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