package com.example.myfirstapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.myfirstapp.Myconstants.Companion.TAG
import kotlinx.android.synthetic.main.activity_google.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*


class BlankFragment : Fragment() {
    private var mview: View? = null
    lateinit var registerForActivityResult:ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                textview.text = it.toString()
            }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mview = inflater.inflate(R.layout.fragment_blank, container, false)
        mview!!.textview.setOnClickListener {
            registerForActivityResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return mview
    }


}