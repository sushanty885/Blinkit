package com.example.adminblinkitclone.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminblinkitclone.Constants
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.Utils
import com.example.adminblinkitclone.adapter.AdapterProduct
import com.example.adminblinkitclone.adapter.CategoriesAdapter
import com.example.adminblinkitclone.databinding.EditProductDialogBinding
import com.example.adminblinkitclone.databinding.FragmentHomeBinding
import com.example.adminblinkitclone.model.Categories
import com.example.adminblinkitclone.model.Product
import com.example.adminblinkitclone.viewmodels.AdminViewModel
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

class homeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var productAdapter: AdapterProduct
    private var selectedImage: Uri? = null
    private val imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            selectedImage = it.data?.data
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setCategories()
        getAllTheProducts("All")
        searchProducts()
        return binding.root
    }


    private fun getAllTheProducts(category: String) {
        binding.shimmerLayout.startShimmer()
        productAdapter = AdapterProduct(::onEditClick)
        binding.rvProducts.adapter = productAdapter
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect {
                if (it.isEmpty()) {
                    binding.rvProducts.isVisible = false
                    binding.tvNoProducts.isVisible = true
                } else {
                    binding.rvProducts.isVisible = true
                    binding.tvNoProducts.isVisible = false
                    productAdapter.differ.submitList(it)
                    productAdapter.originalList = it
                }
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.isVisible = false
                binding.rvProducts.isVisible = true
            }
        }
    }

    private fun onEditClick(product: Product) {
        val dialogBinding = EditProductDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()

        dialogBinding.apply {
            etProductTitle.setText(product.title)
            etProductQuantity.setText(product.quantity.toString())
            etProductUnit.setText(product.unit)
            etProductPrice.setText(product.price.toString())
            etProductStock.setText(product.stock.toString())
            etProductCategory.setText(product.category)
            etProductType.setText(product.productType)

            val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allUnitsOfProduct)
            etProductUnit.setAdapter(unitAdapter)

            val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allCategories.map { it.first })
            etProductCategory.setAdapter(categoryAdapter)

            val productTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allProductType)
            etProductType.setAdapter(productTypeAdapter)

            btnChangeImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                imagePicker.launch(intent)
            }

            btnEdit.setOnClickListener {
                 etProductTitle.isEnabled = true
                etProductQuantity.isEnabled = true
                etProductUnit.isEnabled = true
                etProductPrice.isEnabled = true
                etProductStock.isEnabled = true
                etProductCategory.isEnabled = true
                etProductType.isEnabled = true
            }

            btnSave.setOnClickListener {
                lifecycleScope.launch {
                    val imageBytes = selectedImage?.let { requireContext().contentResolver.openInputStream(it)?.readBytes() }

                    val newImageUrls: List<String>? = if (imageBytes != null) {
                        try {
                            val imagePath = "product_images/${System.currentTimeMillis()}"
                            Utils.supabase.storage.from("PRODUCTS").upload(imagePath, imageBytes, upsert = false)
                            val publicUrl = Utils.supabase.storage.from("PRODUCTS").publicUrl(imagePath)
                            listOf(publicUrl)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                            null
                        }
                    } else {
                        null
                    }

                    if (imageBytes == null || newImageUrls != null) {
                        val updatedProduct = product.copy(
                            title = etProductTitle.text.toString(),
                            quantity = etProductQuantity.text.toString().toIntOrNull() ?: product.quantity,
                            unit = etProductUnit.text.toString(),
                            price = etProductPrice.text.toString().toDoubleOrNull() ?: product.price,
                            stock = etProductStock.text.toString().toIntOrNull() ?: product.stock,
                            category = etProductCategory.text.toString(),
                            productType = etProductType.text.toString(),
                            imageurls = newImageUrls ?: product.imageurls
                        )

                        viewModel.updateProduct(updatedProduct)
                        getAllTheProducts(updatedProduct.category!!)
                        selectedImage = null
                        dialog.dismiss()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun setCategories() {
        val categoryList = ArrayList<Categories>()
        for (category in Constants.allCategories) {
            categoryList.add(Categories(category.first, category.second))
        }
        binding.rvCategories.adapter = CategoriesAdapter(categoryList, ::onCategoryClicked)
    }

    private fun onCategoryClicked(category: Categories) {
        binding.searchEt.text = null
        getAllTheProducts(category.title)
    }

    private fun searchProducts() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    productAdapter.filter.filter(query)
                } else {
                    productAdapter.filter.filter(null)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
