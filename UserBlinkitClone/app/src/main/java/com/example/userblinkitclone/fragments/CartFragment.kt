package com.example.userblinkitclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userblinkitclone.adapter.AdapterCartProducts
import com.example.userblinkitclone.databinding.FragmentCartBinding
import com.example.userblinkitclone.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class CartFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: UserViewModel by activityViewModels()
    private val adapterCartProducts = AdapterCartProducts()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeCartItems()
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                adapterCartProducts.submitList(cartItems)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCartProducts
        }
    }
}
