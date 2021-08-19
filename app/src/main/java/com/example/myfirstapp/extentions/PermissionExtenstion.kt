package com.example.myfirstapp.extentions

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process.myUid
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

enum class PermissionResult{
    SUCCESS,FAILED
}

inline fun FragmentActivity.askForSinglePermissionWithResult(crossinline onPermissionsGranted: (Boolean) -> Unit = {}) =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            onPermissionsGranted(true)
        } else {
            onPermissionsGranted(false)
        }
    }
//region Multiple permissions
inline fun Fragment.askForMultiplePermissions(crossinline onDenied: () -> Unit = {}, crossinline onPermissionsGranted: () -> Unit = {}) =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.map { it.value }.filter { it == false }
        if (granted.isNullOrEmpty()) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }

inline fun FragmentActivity.askForMultiplePermissions(crossinline onDenied: () -> Unit = {}, crossinline onPermissionsGranted: () -> Unit = {}) =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.map { it.value }.filter { it == false }
        if (granted.isNullOrEmpty()) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }

inline fun FragmentActivity.askForMultiplePermissions2(crossinline onPermissionsGranted: (Boolean , MutableMap<String,Boolean>?) -> Unit) =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.map { it.value }.filter { it == false }
        if (granted.isNullOrEmpty()) {
            onPermissionsGranted(true,null)
        } else {
            onPermissionsGranted(false , result)
        }
    }
//endregion

//region Single permission
inline fun Fragment.askForSinglePermission(crossinline onDenied: () -> Unit = {}, crossinline onPermissionsGranted: () -> Unit = {}) =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }

inline fun FragmentActivity.askForSinglePermission(crossinline onDenied: () -> Unit = {}, crossinline onPermissionsGranted: () -> Unit = {}) =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }
//endregion


//region camera permission
inline fun Fragment.getCameraPermission(crossinline onDenied: () -> Unit = {}, crossinline onGranted: () -> Unit = {}) =
    askForSinglePermission(onDenied, onGranted).launch(CAMERA)


inline fun FragmentActivity.getCameraPermission(crossinline onDenied: () -> Unit = {}, crossinline onGranted: () -> Unit = {}) =
    askForSinglePermission(onDenied, onGranted).launch(CAMERA)
//endregion

class AccessibilityContract : ActivityResultContract<Nothing, Nothing>() {
    override fun createIntent(context: Context, input: Nothing?): Intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    override fun parseResult(resultCode: Int, intent: Intent?): Nothing? = null
}
