package com.example.bottomnav.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bottomnav.R

class FragmentSix:Fragment(R.layout.fragment_five) {
    val TAG = "ll"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate: ", )
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        Log.e(TAG, "setMenuVisibility: $menuVisible", )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: 6", )
        Log.i("//", "onViewCreated: 6", )
    }
}