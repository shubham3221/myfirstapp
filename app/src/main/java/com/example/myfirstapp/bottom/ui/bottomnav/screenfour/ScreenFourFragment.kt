package com.example.bottomnav.screenfour

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bottomnav.MainActivity
import com.example.bottomnav.OpenTab
import com.example.bottomnav.R
import kotlinx.android.synthetic.main.screenone_layout.*

class ScreenFourFragment:Fragment(R.layout.screenone_layout) {
    val TAG = "//"
    var listener: OpenTab? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OpenTab
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: screen 4 fragment", )
        openFragmentSix.setOnClickListener {
            listener?.openTab(5)
        }
    }
}