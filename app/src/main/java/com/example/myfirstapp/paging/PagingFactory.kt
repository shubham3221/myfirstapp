package com.example.myfirstapp.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PagingFactory(private val apiService: PagingService) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingViewModel::class.java)){
            return PagingViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown class")
    }
}