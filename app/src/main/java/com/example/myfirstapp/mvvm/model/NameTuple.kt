package com.example.myfirstapp.mvvm.model

import androidx.room.ColumnInfo

data class NameTuple(
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "email") val email: String?
)