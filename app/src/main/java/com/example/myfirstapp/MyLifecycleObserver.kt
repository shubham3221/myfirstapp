package com.example.myfirstapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.mvvm.all_activities.ImageActivity
import com.example.myfirstapp.extra.GpsTracker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task

interface getData{
    fun getImage(uri: Boolean)
}
interface PermissionListener{
    fun result(result:Boolean)
}
interface PermissionsListener{
    fun result(result:Boolean)
}

class MyLifecycleObserver(private val registry : ActivityResultRegistry , val context: Context) :
    DefaultLifecycleObserver {
    lateinit var getPermission : ActivityResultLauncher<String>
    lateinit var nextActivity : ActivityResultLauncher<Intent>
    lateinit var permissions: ActivityResultLauncher<Array<String>>
    lateinit var getCheckLocationOK: ActivityResultLauncher<IntentSenderRequest>
    lateinit var lifecycleOwner: LifecycleOwner
    var listner:getData? = null
    var permission:PermissionListener? = null
    override fun onCreate(owner: LifecycleOwner) {
        lifecycleOwner = owner
        //single permission
        getPermission = registry.register("key", owner, ActivityResultContracts.RequestPermission()) { uri ->

        }

        permissions = registry.register("permissions", owner, ActivityResultContracts.RequestMultiplePermissions()) {

        }

        nextActivity = registry.register("activity",owner,ActivityResultContracts.StartActivityForResult()){
            Log.e(TAG, "onCreate: activity "+it )
        }

        getCheckLocationOK = registry.register("checkGps",lifecycleOwner,ActivityResultContracts.StartIntentSenderForResult()){
            when (it.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    GpsTracker(context)
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    checkGPS()
                }
            }
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


    fun checkGPS() {
        var intentSenderRequest: IntentSenderRequest? = null
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(context as Activity) {
            Log.e("//", "OnSuccess")
            // GPS is ON
          GpsTracker(context)
        }
        task.addOnFailureListener(context, OnFailureListener { e ->
            Log.e("//", "GPS off")
            // GPS off
            if (e is ResolvableApiException) {
                try {
                    intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                    getCheckLocationOK.launch(intentSenderRequest)
                    intentSenderRequest = null
                } catch (e1: IntentSender.SendIntentException) {
                    e1.printStackTrace()
                }
            }
        })

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