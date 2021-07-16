package com.example.myfirstapp.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.mvvm.all_activities.RoomActivity
import com.example.myfirstapp.mvvm.model.DatabaseHelperImpl
import com.example.myfirstapp.mvvm.repo.RoomViewModelNew

class ViewModelFactory(private val dbHelper: DatabaseHelperImpl) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModelNew::class.java)) {
            return RoomViewModelNew(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}