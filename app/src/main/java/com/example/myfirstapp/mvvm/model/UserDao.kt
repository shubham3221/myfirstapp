package com.example.myfirstapp.mvvm.model

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<EntityClass.User>>

    @Query("SELECT * FROM user")
    suspend fun getAllInBackground(): List<EntityClass.User>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserById(id:Int): EntityClass.User

    @Query("SELECT * FROM user WHERE name LIKE :name")
    suspend fun findUserByName(name: String) : List<EntityClass.User>

    @Query("SELECT name, email FROM user LIMIT 2")
    suspend fun showOnly_Name_Email(): List<NameTuple>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<EntityClass.User>):List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(users: EntityClass.User):Long

    @Query("UPDATE user SET mobile = :process WHERE id =:id")
    suspend fun updateUser(id:Int,process:Int)

    @Query("DELETE FROM user WHERE id = :userID")
    suspend fun removeById(userID: Int):Int

    @Query("DELETE FROM user WHERE name = :name")
    suspend fun removeByName(name: String):Int


}