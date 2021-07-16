package com.example.myfirstapp.mvvm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

class EntityClass {

    @Entity(indices = [Index(value = arrayOf("email"), unique = true)])
    data class User(
        @PrimaryKey(autoGenerate = true) val id: Int?,
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "email") val email: String?,
        @ColumnInfo(name = "mobile") var mobile: Int?
    )
    @Entity
    data class UserAuto(
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "email") val email: String?,
        @ColumnInfo(name = "mobile") val mobile: Int?
    ){
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var Id: Int? = null
    }
}