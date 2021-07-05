package com.example.myfirstapp

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LocaleUtils {

    fun oldMethod(context: Context){
        val sharedPreferences = context.getSharedPreferences(
            "mykey",
            AppCompatActivity.MODE_PRIVATE
        )
        Log.e("//", "onCreate: "+sharedPreferences.getString(
            "language",
            "en"
        )!! )
        val locale = Locale(sharedPreferences.getString(
            "language",
            "en"
        )!!)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration = resources.configuration
        context.createConfigurationContext(configuration)
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun setLocale(context: Context, language: String): Context {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else updateResourcesLegacy(context, language)

    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.getResources().getConfiguration()
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.getResources()
        val configuration = resources.getConfiguration()
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        return context
    }



    fun updateConfig(app: Application, configuration: Configuration?) {
//        if (sLocale != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//        val config = app.resources.configuration
//        val locale = Locale("iw")
//        Locale.setDefault(locale)
//        config.locale = locale
//        app.resources.updateConfiguration(config, app.resources.displayMetrics)


        val locale = Locale("iw")
        Locale.setDefault(locale)
        val resources: Resources = app.applicationContext.resources
        val configuration = resources.configuration
        app.applicationContext.createConfigurationContext(configuration)
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}