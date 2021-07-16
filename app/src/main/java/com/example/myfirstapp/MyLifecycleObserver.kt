package com.example.myfirstapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.all_activities.ImageActivity

interface getData{
    fun getImage(uri: Boolean)
}
interface PermissionListener{
    fun result(result:Boolean)
}
interface PermissionsListener{
    fun result(result:Boolean)
}

class MyLifecycleObserver(private val registry : ActivityResultRegistry) :
    DefaultLifecycleObserver {
    lateinit var getPermission : ActivityResultLauncher<String>
    lateinit var nextActivity : ActivityResultLauncher<Intent>
    lateinit var permissions: ActivityResultLauncher<Array<String>>
    var listner:getData? = null
    var permission:PermissionListener? = null
    override fun onCreate(owner: LifecycleOwner) {
        //single permission
        getPermission = registry.register("key", owner, ActivityResultContracts.RequestPermission()) { uri ->

        }

        permissions = registry.register("permissions", owner, ActivityResultContracts.RequestMultiplePermissions()) {

        }

        nextActivity = registry.register("activity",owner,ActivityResultContracts.StartActivityForResult()){
            Log.e(TAG, "onCreate: activity "+it )
        }
    }

    fun selectImage(listner:getData) {
        this.listner = listner
        getPermission.launch("image/*")
    }
    fun getPermission(permission: String,listener: PermissionListener) {
        this.permission = listener
        getPermission.launch(permission)
    }

    fun getPermissions(arr: Array<String>, listener: PermissionListener){
        this.permission = listener
        permissions.launch(arr)
    }

    fun gotoNextActivity(intent:Intent){
        nextActivity.launch(intent)
    }
}

class SimpleContract : ActivityResultContract<Integer, String?>() {

    override fun createIntent(context: Context, input: Integer?): Intent {
        var intent = Intent(context, ImageActivity::class.java)
        intent.putExtra("myInputKey", input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? = when
    {
        resultCode != Activity.RESULT_OK -> null      // Return null, if action is cancelled
        else -> intent?.getStringExtra("data")        // Return the data
    }
}