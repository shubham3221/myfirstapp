package com.example.myfirstapp.extra

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringDef
import androidx.preference.PreferenceManager
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class MyLocaleUtils(val context: Context) {
    companion object{
        const val ENGLISH = "en"
        const val FRENCH = "fr"
        const val SPANISH = "es"
    }
    fun initialize(@LocaleDef defaultLanguage: String?) {
        setLocale(defaultLanguage)
    }

    fun setLocale(@LocaleDef language: String?) {
        updateResources(language)
    }


    private fun updateResources(language: String?) {
        selectedLanguageId = language
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        context.createConfigurationContext(configuration)
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getDefaultSharedPreference(context: Context): SharedPreferences? {
        return if (PreferenceManager.getDefaultSharedPreferences(context) != null
        ) PreferenceManager.getDefaultSharedPreferences(context) else null
    }


    var selectedLanguageId: String?
        get() = getDefaultSharedPreference(context)!!
            .getString("app_language_id", "en")
        set(id) {
            val prefs =
                getDefaultSharedPreference(context)
            val editor = prefs!!.edit()
            editor.putString("app_language_id", id)
            editor.apply()
        }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(ENGLISH, FRENCH, SPANISH)
    annotation class LocaleDef {
        companion object {
            var SUPPORTED_LOCALES = arrayOf(ENGLISH, FRENCH, SPANISH)
        }
    }
}