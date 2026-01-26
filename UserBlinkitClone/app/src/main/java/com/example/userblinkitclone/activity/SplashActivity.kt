package com.example.userblinkitclone.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.userblinkitclone.R
import com.example.userblinkitclone.SplashFragment

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SplashFragment())
                .commitNow()
        }
    }
}