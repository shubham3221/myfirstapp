package com.example.myfirstapp.extra

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.Modelclass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException
import kotlin.Exception
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Restarts an activity from itself with a fade animation
 * Keeps its existing extra bundles and has a intentBuilder to accept other parameters
 */

/**
 * Extension method to startActivity with Animation for Context.
 */
inline fun <reified T : Activity> Context.startActivityWithAnimation(enterResId: Int = 0, exitResId: Int = 0) {
    val intent = Intent(this, T::class.java)
    val bundle = ActivityOptionsCompat.makeCustomAnimation(this, enterResId, exitResId).toBundle()
    ContextCompat.startActivity(this, intent, bundle)
}
fun View.toggleVisibility() : View {
    if (visibility == View.VISIBLE) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
    }
    return this
}
fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun EditText.onAction(action: Int, runAction: (TextView) -> Unit) {
        this.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                action -> {
                    runAction.invoke(v)
                    true
                }
                else -> false
            }
        }
    }

//sharedPref
val Context.myAppPreferences: SharedPreferences
    get() = getSharedPreferences(
        "${this.packageName}_${this.javaClass.simpleName}",
        MODE_PRIVATE
    )

inline fun <reified T : Any> SharedPreferences.getObject(key: String): T? {
    return Gson().fromJson<T>(getString(key, null), T::class.java)
}
inline fun <reified T : Any> SharedPreferences.getObject2(key: String): T? {
    return Gson().fromJson<T>(getString(key, null), object: TypeToken<T>(){}.type)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T {
    return when (T::class) {
        Boolean::class -> getBoolean(key, defaultValue as? Boolean? ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float? ?: 0.0f) as T
        Int::class -> getInt(key, defaultValue as? Int? ?: 0) as T
        Long::class -> getLong(key, defaultValue as? Long? ?: 0L) as T
        String::class -> getString(key, defaultValue as? String? ?: "") as T
        else -> {
            if (defaultValue is Set<*>) {
                getStringSet(key, defaultValue as Set<String>) as T
            } else {
                val typeName = T::class.java.simpleName
                throw Error("Unable to get shared preference with value type '$typeName'. Use getObject")
            }
        }
    }
}




@Suppress("UNCHECKED_CAST")
inline operator fun <reified T : Any> SharedPreferences.set(key: String, value: T) {
    with(edit()) {
        when (T::class) {
            Boolean::class -> putBoolean(key, value as Boolean)
            Float::class -> putFloat(key, value as Float)
            Int::class -> putInt(key, value as Int)
            Long::class -> putLong(key, value as Long)
            String::class -> putString(key, value as String)
            else -> {
                if (value is Set<*>) {
                    putStringSet(key, value as Set<String>)
                } else {
                    val json = Gson().toJson(value)
                    putString(key, json)
                }
            }
        }
        commit()
    }
}

//another sharedpref
inline fun <reified T> Context.sharedPrefs(key: String) = object : ReadWriteProperty<Any?, T> {

    val sharedPrefs by lazy { this@sharedPrefs.getSharedPreferences("APP_DATA", Context.MODE_PRIVATE) }
    val gson by lazy { Gson() }
    var newData: T = (T::class.java).newInstance()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getPrefs()
    }
    fun getValue(): T {
        return getPrefs()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.newData = value
        putPrefs(newData)
    }

    fun putPrefs(value: T?) {
        sharedPrefs.edit {
            when (value) {
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Parcelable -> putString(key, gson.toJson(value))
                else -> throw Throwable("no such type exist to put data")
            }
        }
    }

    fun getPrefs(): T {
        return when (newData) {
            is Int -> sharedPrefs.getInt(key, 0) as T
            is Boolean -> sharedPrefs.getBoolean(key, false) as T
            is String -> sharedPrefs.getString(key, "") as T ?: "" as T
            is Long -> sharedPrefs.getLong(key, 0L) as T
            is Float -> sharedPrefs.getFloat(key, 0.0f) as T
            is Parcelable -> gson.fromJson(sharedPrefs.getString(key, "") ?: "", T::class.java)
            else          -> throw Throwable("no such type exist to put data")
        } ?: newData
    }

}

//another shardpref
inline fun <reified T> SharedPreferences.addItemToList(spListKey: String, item: T) {
    val savedList = getList<T>(spListKey).toMutableList()
    savedList.add(item)
    val listJson = Gson().toJson(savedList)
    edit { putString(spListKey, listJson) }
}

inline fun <reified T> SharedPreferences.removeItemFromList(spListKey: String, item: T) {
    val savedList = getList<T>(spListKey).toMutableList()
    savedList.remove(item)
    val listJson = Gson().toJson(savedList)
    edit {
        putString(spListKey, listJson)
    }
}

fun <T> SharedPreferences.putList(spListKey: String, list: List<T>) {
    val listJson = Gson().toJson(list)
    edit {
        putString(spListKey, listJson)
    }
}

inline fun <reified T> SharedPreferences.getList(spListKey: String): ArrayList<T> {
    val listJson = getString(spListKey, "")
    if (!listJson.isNullOrBlank()) {
        val type = object : TypeToken<ArrayList<T>>() {}.type
        return Gson().fromJson(listJson, type)
    }
    return arrayListOf()
}


//Exception function

fun Exception.toJSONObject(e: Exception): JSONObject? {
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
fun Exception.toJSONString(e: Exception): String? {
    when (e) {
        is SocketException -> "Bad Internet"
        is HttpException -> {
            val response = e.response()
            response?.let {
                it.errorBody()?.let { body->
                    return body.charStream().readText()
                }
            }
        }
        is UnknownHostException -> "Connection Error"
    }
    return null
}

interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T: JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)