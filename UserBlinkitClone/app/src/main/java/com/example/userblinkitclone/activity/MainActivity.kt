package com.example.userblinkitclone.activity

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
import com.example.userblinkitclone.databinding.ActivityMainBinding
import com.example.userblinkitclone.fragments.CartFragment
import com.example.userblinkitclone.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CartListener {

    private lateinit var binding: ActivityMainBinding
    val viewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            viewModel.cartItems.collect {
                showCartLayout(it.size)
            }
        }

        binding.cart.ivCartArrow.setOnClickListener {
            val cartFragment = CartFragment()
            cartFragment.show(supportFragmentManager, "CartFragment")
        }
    }

    override fun showCartLayout(itemCount: Int) {
        val cartBinding = binding.cart
        if (itemCount > 0) {
            cartBinding.cvCart.visibility = View.VISIBLE
            cartBinding.tvCartItemCount.text = "$itemCount items"
        } else {
            cartBinding.cvCart.visibility = View.GONE
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.savingCartItemCount(itemCount)
    }
}