package com.example.bottomnav.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bottomnav.R

class FragmentThree:Fragment(R.layout.fragment_three) {
    val TAG = "//"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: 3", )
    }
}