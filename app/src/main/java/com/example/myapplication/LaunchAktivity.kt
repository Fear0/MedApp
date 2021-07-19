package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class LaunchActivity : AppCompatActivity() {

    // A launch activity for the loading screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        supportActionBar?.hide()
        Handler().postDelayed({
                              val intent = Intent(this, KeyInsertionActivity::class.java)
            startActivity(intent)
            finish()
        },3000)

    }
}