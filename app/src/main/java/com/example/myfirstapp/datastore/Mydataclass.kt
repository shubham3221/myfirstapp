package com.example.myfirstapp.datastore

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class Mydataclass(val context: Context) {
     companion object{
          val mKey= preferencesKey<String>("mKey")
     }
     var dataStore: DataStore<Preferences> = context.createDataStore("my-shared-pref")

     val mValue:Flow<String?>
               get() {
                    return dataStore.data.map {
                         it[mKey]
                    }
               }

     suspend fun saveValue(value:String){
          dataStore.edit {
               it[mKey] = value
          }
     }


}