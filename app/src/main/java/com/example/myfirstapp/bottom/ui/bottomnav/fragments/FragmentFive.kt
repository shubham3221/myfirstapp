package com.example.bottomnav.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bottomnav.R

class FragmentFive:Fragment(R.layout.fragment_five) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("//", "onViewCreated: 5", )
    }
}