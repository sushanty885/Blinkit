package com.example.userblinkitclone.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapter.AdapterProduct
import com.example.userblinkitclone.databinding.FragmentCategoryBinding
import com.example.userblinkitclone.databinding.ItemProductBinding
import com.example.userblinkitclone.model.Product
import com.example.userblinkitclone.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
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
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val category = arguments?.getString("category")
        binding.toolbar.title = category
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        category?.let {
            viewModel.fetchProductsByCategory(it)
        }

        lifecycleScope.launch {
            viewModel.products.collect { products ->
                if (products.isEmpty()) {
                    binding.tvNoProducts.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                } else {
                    binding.tvNoProducts.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                    val adapter = AdapterProduct(products, ::onAddToCartClicked, ::onIncrementClicked, ::onDecrementClicked)
                    binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.rvProducts.adapter = adapter
                }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.searchMenu) {
            findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}