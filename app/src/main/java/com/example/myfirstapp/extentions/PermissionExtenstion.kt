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

class LocationSettingsContract : ActivityResultContract<Nothing, Nothing>() {

    override fun createIntent(context: Context, input: Nothing?): Intent {
        return Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Nothing? {
        return null
    }
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

//region GPS
var enableLocationRetryCount = 1
inline fun Fragment.enableGPS(crossinline onDenied: () -> Unit = {}, crossinline onLocationGranted: () -> Unit = {}) = registerForActivityResult(
    LocationSettingsContract()
) {
    if (enableLocationRetryCount <= 2) {
        onLocationGranted()
        enableLocationRetryCount++
    } else {
        onDenied()
        enableLocationRetryCount = 1
    }
}

inline fun FragmentActivity.enableGPS(crossinline onDenied: () -> Unit = {}, crossinline onLocationGranted: () -> Unit = {}) = registerForActivityResult(
    LocationSettingsContract()
) {
    if (enableLocationRetryCount <= 2) {
        onLocationGranted()
        enableLocationRetryCount++
    } else {
        onDenied()
        enableLocationRetryCount = 1
    }
}
//endregion


//region Foreground location
inline fun Fragment.getForegroundLocationPermission(crossinline onDenied: () -> Unit = {}, crossinline onLocationGranted: () -> Unit = {}) =
    askForMultiplePermissions(onDenied, onLocationGranted).launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))

inline fun FragmentActivity.getForegroundLocationPermission(crossinline onDenied: () -> Unit = {}, crossinline onLocationGranted: () -> Unit = {}) =
    askForMultiplePermissions(onDenied, onLocationGranted).launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
//endregion


//region Background location
inline fun Fragment.getBackgroundLocationPermission(crossinline onDenied: () -> Unit = {}, crossinline onLocationGranted: () -> Unit = {}) =
    when {
        Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
            askForSinglePermission(onDenied, onLocationGranted).launch(ACCESS_BACKGROUND_LOCATION)
        }
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
            getForegroundLocationPermission(onDenied, onLocationGranted)
        }
        Build.VERSION.SDK_INT == Build.VERSION_CODES.R -> {
            getForegroundLocationPermission(onDenied) {
                askForSinglePermission(onDenied, onLocationGranted).launch(ACCESS_BACKGROUND_LOCATION)
            }
        }
        else -> {

        }
    }


inline fun FragmentActivity.getBackgroundLocationPermission(crossinline onDenied: () -> Unit = {}, crossinline onLocationGranted: () -> Unit = {}) =
    when {
        Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
            askForSinglePermission(onDenied, onLocationGranted).launch(ACCESS_BACKGROUND_LOCATION)
        }
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
            getForegroundLocationPermission(onDenied, onLocationGranted)
        }
        Build.VERSION.SDK_INT == Build.VERSION_CODES.R -> {
            getForegroundLocationPermission(onDenied) {
                askForSinglePermission(onDenied, onLocationGranted).launch(ACCESS_BACKGROUND_LOCATION)
            }
        }
        else -> {

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


//call it as tryOrIgnore { registerDocumentContract.launch(videoName.mp4) }
//it's wrapped in try or ignore in case no app can handle the intent
//*/
class CreateVideoContract(private val videoType :String = "video/*") : ActivityResultContract<String?, Uri?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT)
            .setType(videoType)
            .putExtra(Intent.EXTRA_TITLE, input)
    }
    override fun getSynchronousResult(context: Context, input: String?) = null
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}