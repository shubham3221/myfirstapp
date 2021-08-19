package com.example.myfirstapp.music

import LangChanger
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.R
import com.example.myfirstapp.extentions.*
import com.example.myfirstapp.extra.MyLocaleUtils
import com.example.myfirstapp.music.stepMap.GoogleStep2
import com.example.myfirstapp.music.stepMap.StepService
import com.example.myfirstapp.notifications.MyBroadcastReceiver
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_music.*
import org.greenrobot.eventbus.EventBus

class MusicActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)
//        if(savedInstanceState == null) {
//            supportFragmentManager.beginTransaction().replace(R.id.music_container, GoogleStep2()).commitAllowingStateLoss()
//        }

        initToolbar()

        initNavigationDrawer()

        initViewPager()

    }

    private fun initViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(GoogleStep2(), "Home")
        adapter.addFragment(MusicFragment(), "About Us")
        adapter.addFragment(GoogleStep(), "Contact Us")
        viewpager.adapter = adapter
//        viewpager.offscreenPageLimit = 3

        tabs!!.setupWithViewPager(viewpager)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.text!!.toast(this@MusicActivity)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        alarmbtn.setOnClickListener {

            val intent = Intent(this, StepService::class.java)
            intent.action = Myconstants.STOP_SERVICE
            val pendingIntent = PendingIntent.getService(
                this.applicationContext, 234324243, intent, 0
            )
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3 * 1000] = pendingIntent
            Toast.makeText(this, "Alarm will trigger in 3 seconds", Toast.LENGTH_LONG).show()

        }
    }

    private fun initNavigationDrawer() {
        //navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.nav_open,
            R.string.nav_close
        )
        drawerLayout?.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle?.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val navView: NavigationView = findViewById(R.id.navigation)
        navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.nav_logout) {
                startActivity(Intent(this, MusicActivity::class.java))
                "Logout".toast(this)
                drawerLayout?.closeDrawer(GravityCompat.START)
            }
            true
        }
        navView.bringToFront()
    }

    private fun initToolbar() {
        toolbar?.title = getString(R.string.all_songs)

        toolbar?.setOnClickListener {

            LangChanger.getInstance().getLanguage().toast(this)
            LangChanger.getInstance().setLocale(this, MyLocaleUtils.ENGLISH)

            val i = Intent(this, MusicActivity::class.java)
            startActivity(i)
        }
        setSupportActionBar(toolbar)
    }



    fun initSearchView(searchView: SearchView) {
        // Enable Submit button
//        searchView.isSubmitButtonEnabled = true

        // Change search close button image
        val closeButton: ImageView = searchView.findViewById<View>(R.id.search_close_btn) as ImageView
//        closeButton.setImageResource(R.drawable.ic_close)

        // Set hint and the text colors
        val txtSearch = searchView.findViewById<View>(R.id.search_src_text) as EditText
        txtSearch.hint = "Search.."
        txtSearch.setHintTextColor(Color.BLACK)
        txtSearch.setTextColor(resources.getColor(R.color.black))
        txtSearch.setBackgroundColor(Color.WHITE)

        // Set the cursor
        val searchTextView = searchView.findViewById<View>(R.id.search_src_text) as AutoCompleteTextView
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line, arrayOf(
                "abcd",
                "shubham",
                "shubham abc",
                "abcccccc"
            )
        )
        searchTextView.setAdapter(adapter)

        searchTextView.setOnItemClickListener { parent, view, position, id ->
            txtSearch.setText(parent.getItemAtPosition(position).toString())
            txtSearch.setSelection(txtSearch.text.toString().length)
            parent.getItemAtPosition(position).toast(this)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun circleReveal(myView: View, posFromRight: Int, containsOverflow: Boolean, isShow: Boolean) {
        var width: Int = myView.width
        if (posFromRight > 0) width -= posFromRight * resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) - resources.getDimensionPixelSize(
            R.dimen.abc_action_button_min_width_material
        ) / 2
        if (containsOverflow) width -= resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
        val cx = width
        val cy: Int = myView.height / 2
        val anim: Animator = if (isShow) ViewAnimationUtils.createCircularReveal(
            myView,
            cx,
            cy,
            0f,
            width.toFloat()
        ) else ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)
        anim.duration = 220.toLong()

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            }
        })

        // make the view visible and start the animation
        if (isShow) myView.visibility = View.VISIBLE
        anim.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        if (item.itemId == R.id.nav_logout){
            "logout".toast(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // custom method
    private fun showPopup(view: View) {
        val popupMenu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PopupMenu(view.context, view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP_MR1")
        }
        popupMenu.menu.add(0, 0, Menu.NONE, "Item 1")
        popupMenu.menu.add(0, 1, Menu.NONE, "Item 2")
        popupMenu.menu.add(0, 2, Menu.NONE, "Item 3")
        popupMenu.setOnMenuItemClickListener { item ->
            Toast.makeText(view.context, item.title.toString() + "clicked", Toast.LENGTH_SHORT).show()
            true
        }
        popupMenu.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        val menuItem = menu!!.findItem(R.id.action_search)

        val searchView = menuItem!!.actionView as SearchView

        searchView.setOnSearchClickListener {
            circleReveal(searchView, 1, true, isShow = true)
        }

        initSearchView(searchView)
        return true
    }

}