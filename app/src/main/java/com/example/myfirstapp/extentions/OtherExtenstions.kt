package com.example.myfirstapp.extentions

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.util.*



/**
 * Extension method to set Status Bar Color and Status Bar Icon Color Type(dark/light)
 */
enum class StatusIconColorType {
    Dark, Light
}
fun Activity.setStatusBarColor(color: Int, iconColorType: StatusIconColorType = StatusIconColorType.Light) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            statusBarColor = color
            decorView.systemUiVisibility = when (iconColorType) {
                StatusIconColorType.Dark -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                StatusIconColorType.Light -> 0
            }
        }
    } else
        this.window.statusBarColor = color
}
/**
 * Extension method to show toast for Context.
 */
fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, text, duration).show() }

class RequestPermission(
    activity: ComponentActivity,
    private val permission: String,
    onDenied: () -> Unit = {},
    onShowRationale: () -> Unit = {}
) {
    private var onGranted: () -> Unit = {}

    @RequiresApi(Build.VERSION_CODES.M)
    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                activity.shouldShowRequestPermissionRationale(permission) -> {
                    onShowRationale()
                }
                else -> onDenied()
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    fun runWithPermission(onGranted: () -> Unit) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }
}
class RequestMultiplePermissions(
    activity: ComponentActivity,
    private val permission: Array<String>,
    onDenied: (Map<String, Boolean>) -> Unit = {},
    onShowRationale: (Map<String, Boolean>) -> Unit = {}
) {
    private var onGranted: () -> Unit = {}

    @RequiresApi(Build.VERSION_CODES.M)
    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            val pending = isGranted.filterValues { values -> values == false }
            for (i in pending.keys) {
                if(activity.shouldShowRequestPermissionRationale(i)) {
                    onShowRationale(pending)
                    return@registerForActivityResult
                }
            }
            when {
                pending.isNullOrEmpty() -> onGranted()
                else -> onDenied(pending)
            }
        }

    @SuppressLint("NewApi")
    fun runWithPermission(onGranted: () -> Unit) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }
}


fun FragmentActivity.toast(context: Context,msg:String, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, msg, duration).apply { show() }
}
fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}
/**
 * get Application Size in Bytes
 *
 * @property pName the Package Name of the Target Application, Default is Current.
 *
 * Provide Package or will provide the current App Detail
 */
@Throws(PackageManager.NameNotFoundException::class)
fun Context.getAppSize(pName: String = packageName): Long {
    return packageManager.getApplicationInfo(pName, 0).sourceDir.asFile().length()
}

/**
 * get Application Apk File
 *
 * @property pName the Package Name of the Target Application, Default is Current.
 *
 * Provide Package or will provide the current App Detail
 */
@Throws(PackageManager.NameNotFoundException::class)
fun Context.getAppApk(pName: String = packageName): File {
    return packageManager.getApplicationInfo(pName, 0).sourceDir.asFile()
}


fun String.toDate(format: DateFormat): Date? {
    return try {
        format.parse(this)
    } catch (exc: ParseException) {
        exc.printStackTrace()
        null
    }
}
