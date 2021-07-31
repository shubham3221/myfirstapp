package com.example.myfirstapp.mvvm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.model.AppDatabase
import com.example.myfirstapp.mvvm.model.DatabaseHelperImpl
import com.example.myfirstapp.mvvm.model.EntityClass
import com.example.myfirstapp.mvvm.repo.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomViewModel(application: Application) : AndroidViewModel(application) {
    val dbHelper = DatabaseHelperImpl(AppDatabase.OBJECT.getInstance(application))
    var users = MutableLiveData<List<EntityClass.User>>()
    var user = MutableLiveData<EntityClass.User>()
    var singleUser = MutableLiveData<List<EntityClass.User>>()


    val repo: RoomRepository

    init {
        repo = RoomRepository(dbHelper)
    }

    fun getUserById(id: Int) = repo.getUserById(id)
    fun getUserInBackground() = repo.getUsersInBackground()

//    fun fetchUsers() {
//        viewModelScope.launch {
//            try {
//                users.value = dbHelper.getUsers()
//            } catch (e: Exception) {
//                Log.e(TAG, "fetchUsers: ${e.message}")
//            }
//        }
//    }

    fun getSpecificUser(id:Int) = viewModelScope.launch {
            try {
                user.postValue(dbHelper.getUserById(id))
            }catch (e:Exception){
                Log.e(TAG, "getSpecificUser: ${e.message}" )
            }
        }





    fun getUserById2(id:Int) = liveData(Dispatchers.IO) {
        emit("loading")
        try {
            val userById = dbHelper.getUserById(id)
            Log.e(TAG, "getUserById2: " )
            emit(userById)
        }catch (e:Exception){
            Log.e(TAG, "getUserById2: 3" )
            emit(e.message)
        }
    }

    fun insertUsers(user: EntityClass.User) {
        viewModelScope.launch {
            dbHelper.insertAll(listOf(user))
        }
    }

    fun removeValue(id:Int){
        viewModelScope.launch {
            try {
               dbHelper.removeById(id)
            }catch (e:Exception){
                Log.e(TAG, "removeValue: "+e.message )
            }
        }
    }
}