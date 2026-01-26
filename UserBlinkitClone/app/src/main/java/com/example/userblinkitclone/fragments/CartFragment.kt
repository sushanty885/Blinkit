package com.example.userblinkitclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.userblinkitclone.adapter.CartAdapter
import com.example.userblinkitclone.databinding.FragmentCartBinding
import com.example.userblinkitclone.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class CartFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: ProductViewModel by activityViewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                adapter = CartAdapter(cartItems)
                binding.rvCartItems.adapter = adapter
            }
        }
    }
}