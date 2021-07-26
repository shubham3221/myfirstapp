package com.example.myfirstapp.extra

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception

object BasicHelper {
    fun exceptionToJSONObject(e:Exception): JSONObject? {
       if (e is HttpException){
            val response = e.response()
            response?.let {
                it.errorBody()?.let { body->
                    return JSONObject(body.charStream().readText())
                }
            }
        }
        return null
    }

    fun showDialogPermission(activity: Context,pendingPermission: String ,permissionDenied:(Boolean)->Unit) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity, pendingPermission)) {
            Log.e(Myconstants.TAG, "deny click permission: ")
            permissionDenied(true)
        } else {
            val snackbar = Snackbar.make(activity.findViewById(R.id.root),
                "permission needs",
                Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("settings") {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }
            snackbar.show()
            permissionDenied(false)
        }
    }
}