package com.example.myfirstapp

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import java.util.*

class myfirstapp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("//", "onCreate: ")
        LocaleUtils().setLocale(this,"es")

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils().setLocale(this,"es")

    }
}