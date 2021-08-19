package com.example.myfirstapp

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

class TakePictureDemo {
//    fun takePic(){
//        var uri: Uri? = null
//        lateinit var file: File
//        val opencam = registerForActivityResult(ActivityResultContracts.TakePicture()){
//            if (it){
//                Log.e("//", ": " + uri)
//                camera_permission = "0"
//                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri!!))
//                } else {
//                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                }
//            }
//        }
//
//
//        //call
//        file = File.createTempFile("tmp_image_file${System.currentTimeMillis()}", ".png", cacheDir).apply { createNewFile() }
//        uri = FileProvider.getUriForFile(
//            this,
//            applicationContext.packageName + ".provider",
//            file
//        )
//
//        opencam.launch(uri)
//    }



    //path
//    <?xml version="1.0" encoding="utf-8"?>
//    <paths xmlns:android="http://schemas.android.com/apk/res/android">
//    <root-path name="root" path="." />
//    <external-path name="external_files" path="."/>
//    <external-files-path name="my_images" path="Pictures" />
//    </paths>
}