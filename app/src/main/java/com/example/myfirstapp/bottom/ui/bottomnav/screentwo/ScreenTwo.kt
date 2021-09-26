package com.example.bottomnav.screentwo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bottomnav.R
import com.example.bottomnav.ViewPagerAdapter
import com.example.bottomnav.screenfour.ScreenFourFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_two.*

class ScreenTwo:Fragment(R.layout.fragment_two) {
    val TAG = "//"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: screen two", )
        val fragmentList = arrayListOf(ScreenTwoFragment(), ScreenTwoFragment())
        val fragmentName = arrayListOf("Screen one", "Screen Two")
        val viewPagerAdapter = ViewPagerAdapter(requireActivity(), fragmentList as ArrayList<Fragment>)
        viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout,viewPager2){view,pos->
            view.text = fragmentName[pos]
        }.attach()
    }
}