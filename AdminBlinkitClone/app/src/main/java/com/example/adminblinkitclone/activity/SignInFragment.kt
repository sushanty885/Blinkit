package com.example.adminblinkitclone.activity


import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.SupabaseClient.supabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val signInButton = view.findViewById<Button>(R.id.signInButton)
        val signUpButton = view.findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                try {
                    supabase.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    Toast.makeText(requireContext(), "Sign up successful! Please check your email for verification.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Sign up failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                try {
                    supabase.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }
                    Toast.makeText(requireContext(), "Sign in successful!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    requireActivity().finish()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
