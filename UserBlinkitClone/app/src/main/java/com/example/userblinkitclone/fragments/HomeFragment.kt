package com.example.userblinkitclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userblinkitclone.Constants
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapter.AdapterProduct
import com.example.userblinkitclone.adapter.AdapterCategory
import com.example.userblinkitclone.databinding.FragmentHomeBinding
import com.example.userblinkitclone.databinding.ItemProductBinding
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.models.Category
import com.example.userblinkitclone.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var adapterProduct: AdapterProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductAdapter()
        setCategories()
        observeBestsellersAndCart()

        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setupProductAdapter() {
        adapterProduct = AdapterProduct(::onAddToCartClicked, ::onIncrementClicked, ::onDecrementClicked)
        binding.rvBestsellers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBestsellers.adapter = adapterProduct
    }

    private fun observeBestsellersAndCart() {
        viewModel.fetchAllProducts()
        lifecycleScope.launch {
            viewModel.products.collect { products ->
                adapterProduct.submitList(products)
            }
        }
        lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                adapterProduct.setCartProducts(cartItems)
            }
        }
    }

    private fun onAddToCartClicked(product: Product, productBinding: ItemProductBinding) {
        productBinding.btnAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE
        viewModel.addToCart(product)
    }

    private fun onIncrementClicked(product: Product, productBinding: ItemProductBinding) {
        val count = productBinding.tvProductCount.text.toString().toInt()
        productBinding.tvProductCount.text = (count + 1).toString()
        viewModel.addToCart(product)
    }

    private fun onDecrementClicked(product: Product, productBinding: ItemProductBinding) {
        val count = productBinding.tvProductCount.text.toString().toInt()
        viewModel.removeProduct(product)
        if (count > 1) {
            productBinding.tvProductCount.text = (count - 1).toString()
        } else {
            productBinding.btnAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
        }
    }

    private fun setCategories(){
        val categoryList = ArrayList<Category>()
        for (i in 0 until Constants.allProductsCategory.size) {
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]))
        }
        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvCategories.adapter = AdapterCategory(categoryList) {
            val bundle = Bundle()
            bundle.putString("category", it.title)
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
        }
    }
}
