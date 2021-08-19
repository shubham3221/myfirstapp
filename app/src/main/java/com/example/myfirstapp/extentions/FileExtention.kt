package com.example.myfirstapp.extentions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun Activity.createImageFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("dd_MM_yyyy_HHmmss").format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}