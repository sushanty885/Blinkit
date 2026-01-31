package com.example.userblinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.ActivityUsersMainBinding
import com.example.userblinkitclone.fragments.CartFragment
import com.example.userblinkitclone.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class UsersMainActivity : AppCompatActivity(), CartListener {

    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                showCartLayout(cartItems.size)
            }
        }

        binding.cart.ivCartArrow.setOnClickListener {
            val cartFragment = CartFragment()
            cartFragment.show(supportFragmentManager, "CartFragment")
        }

        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.cart.tvNext.setOnClickListener { 
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    override fun showCartLayout(itemCount: Int) {
        val cartBinding = binding.cart
        if (itemCount > 0) {
            cartBinding.cvCart.visibility = View.VISIBLE
            cartBinding.tvCartItemCount.text = "$itemCount items"
            cartBinding.tvNext.visibility = View.VISIBLE
        } else {
            cartBinding.cvCart.visibility = View.GONE
            cartBinding.tvNext.visibility = View.GONE
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.savingCartItemCount(itemCount)
    }
}
