package com.example.userblinkitclone.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapter.AdapterProduct
import com.example.userblinkitclone.databinding.FragmentSearchBinding
import com.example.userblinkitclone.databinding.ItemProductBinding
import com.example.userblinkitclone.model.Product
import com.example.userblinkitclone.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: ProductViewModel by activityViewModels()
    private lateinit var cartListener: CartListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener) {
            cartListener = context
        } else {
            throw ClassCastException("Please implement CartListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    viewModel.searchProducts(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            viewModel.searchResults.collect {
                val adapter = AdapterProduct(it, ::onAddToCartClicked, ::onIncrementClicked, ::onDecrementClicked)
                binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvProducts.adapter = adapter
            }
        }
    }

    private fun onAddToCartClicked(product: Product, productBinding: ItemProductBinding) {
        productBinding.btnAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE
        viewModel.addToCart(product)
        cartListener.showCartLayout(viewModel.getCartItemCount())
        cartListener.savingCartItemCount(viewModel.getCartItemCount())
    }

    private fun onIncrementClicked(product: Product, productBinding: ItemProductBinding) {
        val count = productBinding.tvProductCount.text.toString().toInt()
        productBinding.tvProductCount.text = (count + 1).toString()
        viewModel.addToCart(product)
        cartListener.showCartLayout(viewModel.getCartItemCount())
        cartListener.savingCartItemCount(viewModel.getCartItemCount())
    }

    private fun onDecrementClicked(product: Product, productBinding: ItemProductBinding) {
        val count = productBinding.tvProductCount.text.toString().toInt()
        viewModel.removeProduct(product)
        cartListener.showCartLayout(viewModel.getCartItemCount())
        cartListener.savingCartItemCount(viewModel.getCartItemCount())
        if (count > 1) {
            productBinding.tvProductCount.text = (count - 1).toString()
        } else {
            productBinding.btnAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
        }
    }
}