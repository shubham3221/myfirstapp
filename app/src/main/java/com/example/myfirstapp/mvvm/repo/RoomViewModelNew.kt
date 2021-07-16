package com.example.myfirstapp.mvvm.repo

import androidx.lifecycle.ViewModel
import com.example.myfirstapp.mvvm.model.DatabaseHelperImpl
import com.example.myfirstapp.mvvm.model.EntityClass

class RoomViewModelNew(dbHelper: DatabaseHelperImpl) : ViewModel() {
    private val repository = RoomRepository(dbHelper)

    fun liveData() = repository.allUsers()

    fun insertUser(user:EntityClass.User) = repository.insertUser(user)

    fun updateUser(id:Int, process: Int) = repository.updateUser(id,process)

    suspend fun updateUserAsync(id:Int, process: Int) = repository.updateUserAsync(id,process)

    fun removeByID(id:Int) = repository.removeUserByID(id)

    fun removeByName(name:String) = repository.removeUserByName(name)

    fun deleteAll() = repository.deleteAll()

    fun findByName(name:String) = repository.findUserByName(name)

    fun showOnlyNameEmail() = repository.showOnlyNameEmail()
}