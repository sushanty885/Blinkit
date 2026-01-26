package com.example.adminblinkitclone.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.adminblinkitclone.Constants
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.Utils
import com.example.adminblinkitclone.adapter.AdapterSelectedImage
import com.example.adminblinkitclone.databinding.FragmentAddProductBinding
import com.example.adminblinkitclone.model.Product
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch

class addProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private val imageUris = ArrayList<Uri>()
    private val selectImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            imageUris.clear()
            imageUris.addAll(it.take(5))
            val adapter = AdapterSelectedImage(imageUris)
            binding.rvProductImages.adapter = adapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        setStatusBarColor()
        setAutoCompleteTextViews()
        onImageSelectClicked()
        binding.btnAddProduct.setOnClickListener { saveProduct() }

        return binding.root
    }

    private fun saveProduct() {
        val productTitle = binding.etProductTitle.text.toString().trim()
        val productQuantityStr = binding.etProductQuantity.text.toString().trim()
        val productUnit = binding.etProductUnit.text.toString().trim()
        val productPriceStr = binding.etProductPrice.text.toString().trim()
        val productStockStr = binding.etProductStock.text.toString().trim()
        val productCategory = binding.etProductCategory.text.toString().trim()
        val productType = binding.etProductType.text.toString().trim()

        if (productTitle.isBlank() || productQuantityStr.isBlank() || productUnit.isBlank() || productPriceStr.isBlank() || productStockStr.isBlank() || productCategory.isBlank() || productType.isBlank()) {
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUris.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_SHORT).show()
            return
        }

        val productQuantity = productQuantityStr.toIntOrNull()
        val productPrice = productPriceStr.toDoubleOrNull()
        val productStock = productStockStr.toIntOrNull()

        if (productQuantity == null || productPrice == null || productStock == null) {
            Toast.makeText(requireContext(), "Please enter valid numbers for quantity, price, and stock", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            Toast.makeText(requireContext(), "Uploading images...", Toast.LENGTH_SHORT).show()
            val imageurls = ArrayList<String>()
            try {
                for (uri in imageUris) {
                    val imagePath = "product_images/${System.currentTimeMillis()}"
                    val bytes = requireContext().contentResolver.openInputStream(uri)!!.readBytes()
                    Utils.supabase.storage.from("PRODUCTS").upload(imagePath, bytes)
                    val url = Utils.supabase.storage.from("PRODUCTS").publicUrl(imagePath)
                    imageurls.add(url)
                }

                val product = Product(
                    title = productTitle,
                    quantity = productQuantity,
                    unit = productUnit,
                    price = productPrice,
                    stock = productStock,
                    category = productCategory,
                    productType = productType,
                    imageurls = imageurls
                )

                Utils.supabase.from("Products").insert(product)

                Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearFields() {
        binding.etProductTitle.text?.clear()
        binding.etProductQuantity.text?.clear()
        binding.etProductUnit.text?.clear()
        binding.etProductPrice.text?.clear()
        binding.etProductStock.text?.clear()
        binding.etProductCategory.text?.clear()
        binding.etProductType.text?.clear()
        imageUris.clear()
        binding.rvProductImages.adapter = null
    }

    private fun onImageSelectClicked() {
        binding.ivAddImage.setOnClickListener {
            selectImages.launch("image/*")
        }
    }

    private fun setAutoCompleteTextViews() {
        val categoryNames = Constants.allCategories.map { it.first }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.show_category_layout,
            categoryNames
        )
        binding.etProductCategory.setAdapter(adapter)

        val unitAdapter = ArrayAdapter(
            requireContext(),
            R.layout.show_category_layout,
            Constants.allUnitsOfProduct
        )
        binding.etProductUnit.setAdapter(unitAdapter)

        val typeAdapter = ArrayAdapter(requireContext(), R.layout.show_category_layout, Constants.allProductType)
        binding.etProductType.setAdapter(typeAdapter)
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
