package com.example.myfirstapp.mvvm.model


class DatabaseHelperImpl(private val appDatabase: AppDatabase) {

    fun getUsers() = appDatabase.userDao().getAll()

    suspend fun getUsersInBackground() = appDatabase.userDao().getAllInBackground()

    suspend fun getUserById(id:Int) = appDatabase.userDao().getUserById(id)

    suspend fun insertAll(users: List<EntityClass.User>) = appDatabase.userDao().insertAll(users)

    suspend fun insertSingleUser(users: EntityClass.User) = appDatabase.userDao().insertUser(users)

    suspend fun updateUser(id:Int,process: Int) = appDatabase.userDao().updateUser(id,process)

    suspend fun removeById(userID:Int) = appDatabase.userDao().removeById(userID)

    suspend fun removeByName(name:String) = appDatabase.userDao().removeByName(name)

    fun deleteAll() = appDatabase.clearAllTables()

    suspend fun findUserByName(name:String) = appDatabase.userDao().findUserByName(name)

    suspend fun showOnlynameEmail() = appDatabase.userDao().showOnly_Name_Email()


}