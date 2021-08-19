package com.example.myfirstapp.music.stepMap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.GoogleStepFragmentBinding
import com.google.android.gms.maps.GoogleMap

class TestingFragment: Fragment() , MapReady {

    lateinit var binding : GoogleStepFragmentBinding

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GoogleStepFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapsStep = MapsSetupFramgent(this)
        mapsStep.initMap()
    }

    override fun onMapReady(p0: GoogleMap) {
        Log.e(TAG, "onMapReady: " )
    }

}