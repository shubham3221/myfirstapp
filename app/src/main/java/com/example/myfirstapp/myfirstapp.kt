package com.example.myfirstapp

import android.app.Application
import android.content.res.Configuration
import android.util.Log

class myfirstapp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("//", "onCreate: ")
        LocaleUtilsFirst().setLocale(this,"es")

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtilsFirst().setLocale(this,"es")

    }
}