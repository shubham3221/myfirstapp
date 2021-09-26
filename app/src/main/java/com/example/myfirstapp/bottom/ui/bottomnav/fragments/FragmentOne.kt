package com.example.bottomnav.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bottomnav.R

class FragmentOne:Fragment(R.layout.fragment_one) {
    val TAG = "//"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
        Log.i(TAG, "onViewCreated: 1", )
        }
    }
}