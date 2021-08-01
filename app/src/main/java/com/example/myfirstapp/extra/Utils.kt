package com.example.myfirstapp

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import org.json.JSONObject
import java.io.File

enum class Status2 {
    SUCCESS,
    ERROR,
    LOADING
}

data class MyResult2<out T>(val status: Status2, val data: T? , val message: String? , val jsonObject: JSONObject?) {

    constructor(status: Status2) : this(status, null, null,null)
    constructor(status: Status2,message: String?) : this(status, null, message,null)
    constructor(status: Status2, data: T?) : this(status, data, null,null)
    constructor(status: Status2, data: T?,message: String?) : this(status, data, message,null)
    constructor(status: Status2, jsonObject: JSONObject?, message: String?) : this(status, null, message,null)

    companion object {
        fun <T> success(data: T?): MyResult2<T> = MyResult2(status = Status2.SUCCESS, data = data)
        fun <T> success(data: T? , message: String?): MyResult2<T> = MyResult2(status = Status2.SUCCESS, data = data , message)

        fun <T> error(data: T?,jsonObject: JSONObject , message: String): MyResult2<T> =
            MyResult2(status = Status2.ERROR,jsonObject = jsonObject, message = message)

        fun <T> error(data: T? , message: String): MyResult2<T> =
            MyResult2(status = Status2.ERROR, data = data, message = message)

        fun <T> loading(): MyResult2<T> = MyResult2(status = Status2.LOADING)
        fun <T> loading(message: String?): MyResult2<T> = MyResult2(status = Status2.LOADING, message = message)
    }
}
object Utils {
    fun getRealPath_1(context: Context, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val string = cursor.getString(column_index)
            string
        } finally {

            cursor?.close()
        }
    }

    fun getRealPath_2(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }

    fun getRealPathFromRecent(context: Context, contentUri: Uri): String? {
        // Will return "image:x*"
        // Will return "image:x*"
        val wholeID = DocumentsContract.getDocumentId(contentUri)
        val id = wholeID.split(":").toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor: Cursor =
            context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)!!

        var filePath: String? = ""

        val columnIndex = cursor.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }

        cursor.close()
        return filePath
    }

    fun getRealPathAllinOne(context: Context, uri: Uri): File? {
        return if (uri.toString().substring(0, 21) == "content://com.android") {
            val photo_split = uri.toString().split("%3A");
            val imageURI = "content://media/external/images/media/" + photo_split[1];
            File(getRealPath_1(context, Uri.parse(imageURI)))
        }else{
            File(getRealPath_2(context,uri))
        }
    }


}