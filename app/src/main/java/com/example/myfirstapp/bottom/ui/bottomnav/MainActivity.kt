package com.example.bottomnav

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.*
import com.example.bottomnav.fragments.*
import com.example.bottomnav.screenfour.ScreenFour
import com.example.bottomnav.screentwo.ScreenTwo
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager




interface OpenTab{
    fun openTab(pos:Int)
}

class MainActivity : AppCompatActivity(),OpenTab {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentList = arrayListOf(
            FragmentOne(), ScreenTwo(),FragmentThree(),ScreenFour(),FragmentFive(),FragmentSix()
        )
        val viewPagerAdapter = ViewPagerAdapter2(supportFragmentManager, fragmentList)
        viewPager.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT
        viewPager.adapter = viewPagerAdapter
        viewPager.setPagingEnabled(false)

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.one-> {
                    viewPager.currentItem = 0
                }
                R.id.two-> {
                    viewPager.currentItem = 1
                }
                R.id.three->{
                    viewPager.currentItem = 2
                }
                R.id.four-> {
                    viewPager.currentItem = 3
                    viewPager.adapter?.notifyDataSetChanged()
                }
                R.id.five-> {
                    viewPager.currentItem = 4
                }
            }
            true
        }


    }

    override fun openTab(pos: Int) {
        viewPager.adapter?.notifyDataSetChanged()
        viewPager.setCurrentItem(pos,false)
    }
}

class ViewPagerAdapter(fa: FragmentActivity, private val fragments:ArrayList<Fragment>): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int =fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}
class ViewPagerAdapter2(fa: FragmentManager, private val fragments:ArrayList<Fragment>): FragmentStatePagerAdapter(fa,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getItemPosition(`object`: Any): Int {
        if (`object` is FragmentSix){
            return POSITION_NONE
        }
        return super.getItemPosition(`object`)
    }

}

class CustomViewPager(context: Context, attrs: AttributeSet) :
    ViewPager(context, attrs) {
    private var isEnable = true
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isEnable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isEnable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isEnable = enabled
    }
}