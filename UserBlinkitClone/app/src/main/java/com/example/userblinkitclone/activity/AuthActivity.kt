package com.example.userblinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.databinding.ActivityAuthBinding
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Utils.getSupabase().auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AuthActivity, "Sign in successful", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@AuthActivity, UsersMainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AuthActivity,
                            "Sign in failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Utils.getSupabase().auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AuthActivity,
                            "Sign up successful. Check your email for confirmation.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AuthActivity,
                            "Sign up failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}