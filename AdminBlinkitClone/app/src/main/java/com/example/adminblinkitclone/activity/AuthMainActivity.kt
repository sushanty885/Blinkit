package com.example.adminblinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class AuthMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val signInButton = findViewById<Button>(R.id.signInButton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                try {
                    SupabaseClient.supabase.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    Toast.makeText(this@AuthMainActivity, "Sign up successful! Please check your email for verification.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@AuthMainActivity, "Sign up failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                try {
                    SupabaseClient.supabase.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    Toast.makeText(this@AuthMainActivity, "Sign in successful!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@AuthMainActivity, AuthMainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@AuthMainActivity, "Sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}