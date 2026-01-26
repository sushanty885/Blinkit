package com.example.adminblinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
                SupabaseClient.supabase.auth.sessionStatus.collect {
                    when (it) {
                        is io.github.jan.supabase.gotrue.SessionStatus.Authenticated -> {
                            startActivity(Intent(this@SplashActivity, AdminMainActivity::class.java))
                            finish()
                        }
                        is io.github.jan.supabase.gotrue.SessionStatus.NotAuthenticated -> {
                            startActivity(Intent(this@SplashActivity, AuthMainActivity::class.java))
                            finish()
                        }
                        else -> {}
                    }
                }
            }
        }, 3000)
    }
}