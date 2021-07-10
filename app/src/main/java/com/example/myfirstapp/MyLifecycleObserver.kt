package com.example.myfirstapp

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.myfirstapp.Myconstants.Companion.TAG

interface getData{
    fun getImage(uri: Boolean)
}

class MyLifecycleObserver(private val registry : ActivityResultRegistry) :
    DefaultLifecycleObserver {
    lateinit var getContent : ActivityResultLauncher<String>
    lateinit var nextActivity : ActivityResultLauncher<Intent>
    var listner:getData? = null
    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, ActivityResultContracts.RequestPermission()) { uri ->
            // Handle the returned Uri
            Log.e(TAG, "onCreate: uri "+uri )
//            listner!!.getImage(uri)

        }

        nextActivity = registry.register("activity",owner,ActivityResultContracts.StartActivityForResult()){
            Log.e(TAG, "onCreate: activity "+it )
        }
    }

    fun selectImage(listner:getData) {
        this.listner = listner
        getContent.launch("image/*")
    }
    fun getPermission() {
        getContent.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun gotoNextActivity(intent:Intent){
        nextActivity.launch(intent)
    }
}