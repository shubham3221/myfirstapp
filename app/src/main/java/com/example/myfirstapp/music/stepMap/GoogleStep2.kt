package com.example.myfirstapp.music.stepMap

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.googleMaps.helper.MapsAnimation
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.GoogleStepFragmentBinding
import com.example.myfirstapp.extentions.makeApiCallAsync
import com.example.myfirstapp.extentions.slideUpScaleAnimation
import com.example.myfirstapp.extentions.toast
import com.example.myfirstapp.extra.getObject
import com.example.myfirstapp.music.MusicFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import java.util.*


class GoogleStep2 : Fragment() , MapReady {

    private lateinit var mMap: GoogleMap
    lateinit var binding : GoogleStepFragmentBinding
    //service
    var mService: StepService? = null
    var mIsBound: Boolean? =null
    var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG, "onServiceConnected: ")
            val binder = service as StepService.MyStepbinder
            mService = binder.service
            mService?.mMap = mMap
            mIsBound = true
            startStepService()


            binding.startserice.setOnClickListener {
//                gotoNextFragment()
//            mService?.getLastKnownLocation()
//            return@setOnClickListener
                mService?.setliveLocation = true
                mService?.setCurrentLatlong(LatLng(30.3752, 76.7821))

                activity?.getSharedPreferences("latlng", AppCompatActivity.MODE_PRIVATE)?.getObject<List<List<Double>>>("values")?.let { list->
                    var mutableList = mutableListOf<LatLng>()
                    for (i in list.indices){
                        mutableList.add(LatLng(list[i][1], list[i][0]))
                    }
                    MapsAnimation.addPolylineWithAnimation(mMap, mutableList, 0)
                    MapsAnimation.showCurvedPolyline(mMap, mutableList[0], mutableList[mutableList.size - 1], 0.5)
                }
            }

            mService?.stepsLiveData?.observe(this@GoogleStep2){
                binding.steps.text = it
            }

            mService?.distanceLiveData?.observe(this@GoogleStep2){
                binding.distance.text = it
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIsBound = false
            Log.e(TAG, "onServiceDisconnected: ")
        }
    }

    private fun gotoNextFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.popup_show, R.anim.popup_hide)
    //                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.fade_in, android.R.anim.fade_out)
    //                    .setCustomAnimations(R.anim.anim_scale_in,R.anim.anim_scale_out,R.anim.anim_scale_in,R.anim.anim_scale_out)
                .add(R.id.music_container, MusicFragment())
                .addToBackStack(null)
    //                      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

                .commitAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GoogleStepFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsSetupFramgent(this).initMap()
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        val sheet = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet)
        binding.bottomSheet.textViewFacebook.setOnClickListener {
            "facebook".toast(requireContext())
            binding.bottomSheet.bottomSheet.slideUpScaleAnimation()
            sheet.isFitToContents = true
            sheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    override fun onMapReady(p0: GoogleMap) {
        Log.e(TAG, "onMapReady: 2", )
        mMap = p0
        doBindService()
    }

    private fun startStepService() {
        val intent = Intent(requireActivity(), StepService::class.java)
        requireActivity().startService(intent)
    }

    private fun doBindService() {
        val intent = Intent(requireActivity(), StepService::class.java)
        requireActivity().bindService(intent, serviceConnection, AppCompatActivity.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        requireActivity().unbindService(serviceConnection)
        super.onDestroy()
    }

}