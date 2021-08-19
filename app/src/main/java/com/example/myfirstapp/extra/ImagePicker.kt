package com.example.myfirstapp.extra

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.util.*

object ImagePicker {
    fun getPickImageChooserIntent(
            context: Context,
            title: CharSequence?,
            includeDocuments: Boolean,
            includeCamera: Boolean
    ): Intent? {
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager = context.packageManager

        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager)!!)
        }
        var galleryIntents = getGalleryIntents(
                packageManager,
                Intent.ACTION_GET_CONTENT,
                includeDocuments
        )
        if (galleryIntents?.size == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(
                    packageManager,
                    Intent.ACTION_PICK,
                    includeDocuments
            )
        }
        allIntents.addAll(galleryIntents!!)
        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = allIntents[allIntents.size - 1]
            allIntents.removeAt(allIntents.size - 1)
        }

        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(target, title)

        // Add all other intents
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray()
        )
        return chooserIntent
    }
    fun isExplicitCameraPermissionRequired(context: Context): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasPermissionInManifest(context, "android.permission.CAMERA")
                && (context.checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED))
    }
    private fun getGalleryIntents(
            packageManager: PackageManager, action: String, includeDocuments: Boolean): List<Intent>? {
        val intents: MutableList<Intent> = ArrayList()
        val galleryIntent = if (action === Intent.ACTION_GET_CONTENT) Intent(action) else Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }

        // remove documents intent
        if (!includeDocuments) {
            for (intent in intents) {
                if (intent.component?.className
                        == "com.android.documentsui.DocumentsActivity") {
                    intents.remove(intent)
                    break
                }
            }
        }
        return intents
    }

    /** Get all Camera intents for capturing image using device camera apps.  */
    private fun getCameraIntents(
            context: Context, packageManager: PackageManager): List<Intent>? {
        val allIntents: MutableList<Intent> = ArrayList()

        // Determine Uri of camera image to  save.
        val outputFileUri = getCaptureImageOutputUri(context)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
            allIntents.add(intent)
        }
        return allIntents
    }

    /**
     * Get URI to image received from capture by camera.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     */
    private fun getCaptureImageOutputUri(context: Context): Uri? {
        var outputFileUri: Uri? = null
        val getImage = context.externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

    /**
     * Check if the app requests a specific permission in the manifest.
     *
     * @param permissionName the permission to check
     * @return true - the permission in requested in manifest, false - not.
     */
    private fun hasPermissionInManifest(
            context: Context, permissionName: String): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermisisons = packageInfo.requestedPermissions
            if (declaredPermisisons != null && declaredPermisisons.size > 0) {
                for (p in declaredPermisisons) {
                    if (p.equals(permissionName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }
}