package com.example.myfirstapp.music

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.extentions.*
import com.example.myfirstapp.extra.BasicHelper
import com.example.myfirstapp.extra.MyLocaleUtils
import com.example.myfirstapp.mvvm.model.Mymodel
import kotlinx.android.synthetic.main.activity_music.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)


        toolbar?.title = getString(R.string.all_songs)
        toolbar?.setOnClickListener {
            if (LocaleHelper.getLanguage(this).equals(MyLocaleUtils.ENGLISH)){
                LocaleHelper.setLocale(this,MyLocaleUtils.SPANISH)
            }else{
                LocaleHelper.setLocale(this,MyLocaleUtils.ENGLISH)
            }
            val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
            startActivity(i)
        }
        setSupportActionBar(toolbar)
        if(savedInstanceState == null) { // initial transaction should be wrapped like this
            supportFragmentManager.beginTransaction()
                .replace(R.id.music_container, StepsFragment())
                .commitAllowingStateLoss()
        }
    }
}