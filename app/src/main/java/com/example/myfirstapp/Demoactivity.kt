package com.example.myfirstapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.demo_layout_mainactivity.*

class Demoactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_layout_mainactivity)

        button.setOnClickListener {
            startActivity(Intent(this,Lastscreen::class.java))
        }
    }
}