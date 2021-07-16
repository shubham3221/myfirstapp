package com.example.myfirstapp.mvvm.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.model.DatabaseHelperImpl
import com.example.myfirstapp.mvvm.model.EntityClass
import com.example.myfirstapp.mvvm.viewmodel.MyResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class RoomRepository(val dbHelper: DatabaseHelperImpl) {
    var users = MutableLiveData<List<EntityClass.User>>()
    var user = MutableLiveData<EntityClass.User>()

    fun insertUser(model: EntityClass.User) = liveData(Dispatchers.IO) {
        try {
            val insert = dbHelper.insertSingleUser(model)
            emit(insert)
        }catch (e:Exception){
            emit(e.message)
        }
    }

    fun updateUser(id:Int,process:Int) = liveData(Dispatchers.IO) {
        try {
            emit(dbHelper.updateUser(id,process))
        }catch (e:Exception){
            emit(e.message)
        }
    }
    suspend fun updateUserAsync(id:Int, process:Int) = run {
            val async = CoroutineScope(Dispatchers.IO).async {
                dbHelper.updateUser(id, process)
            }
        async.await()
    }


    fun allUsers() = dbHelper.getUsers()

    fun removeUserByID(id: Int) = liveData(Dispatchers.IO) {
        try {
            val removeById = dbHelper.removeById(id)
            if (removeById <= 0) emit("Not Found") ?: emit(removeById)
        }catch (e:Exception){
            emit(e.message)
        }
    }

    fun removeUserByName(name: String) = liveData(Dispatchers.IO) {
        try {
            val removeById = dbHelper.removeByName(name)
            emit(removeById)
        }catch (e:Exception){
            emit(e.message)
        }
    }

    fun deleteAll() = liveData(Dispatchers.IO) {
        try {
            emit(dbHelper.deleteAll())
        }catch (e:Exception){
            emit(e.message)
        }
    }

    fun findUserByName(name:String) = liveData(Dispatchers.IO) {
        try {
            val findUserByName = dbHelper.findUserByName(name)
            emit(findUserByName)
        }catch (e:Exception){
            Log.e(TAG, "findUserByName: "+e.message )
        }
    }

    fun showOnlyNameEmail() = liveData(Dispatchers.IO) {
        emit(dbHelper.showOnlynameEmail())
    }






    fun getUserById(id: Int) = liveData(Dispatchers.IO) {
        try {
            Log.e(TAG, "getUserById: 1")
            val userById = dbHelper.getUserById(id)
            if (userById == null) {
                emit(MyResult.error(null, "errorrr" ?: "error"))
            } else {
                emit(MyResult.success(dbHelper.getUserById(id)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserById: 2" + e.message)
            emit(MyResult.error(null, "errorrr" ?: "error"))
        }
    }
    fun getUsersInBackground() = liveData(Dispatchers.IO) {
        try {
            emit(MyResult.success(dbHelper.getUsersInBackground()))
        } catch (e: Exception) {
            emit(MyResult.error(null, e.message ?: "error"))
        }
    }




}