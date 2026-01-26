package com.example.adminblinkitclone.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminblinkitclone.Utils
import com.example.adminblinkitclone.model.Product
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _downloadedUrls = MutableStateFlow<List<String>>(emptyList())
    val downloadedUrls = _downloadedUrls.asStateFlow()

    fun fetchAllTheProducts(category: String): Flow<List<Product>> = flow {
        val productList = try {
            Utils.supabase.from("Products").select {
                if (category != "All") {
                    filter {
                        eq("category", category)
                    }
                }
                order("createdat", Order.DESCENDING)
            }.decodeList<Product>()
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error fetching products: ${e.message}")
            emptyList<Product>()
        }
        emit(productList)
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                Utils.supabase.from("Products").update(product) { filter {
                    eq("id", product.id!!)
                } }
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error updating product: ${e.message}")
            }
        }
    }

    fun saveImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            try {
                val fileName = "product_images/${UUID.randomUUID()}"
                val path = Utils.supabase.storage.from("PRODUCTS").upload(fileName, imageBytes, upsert = false)
                val publicUrl = Utils.supabase.storage.from("PRODUCTS").publicUrl(path)
                val currentUrls = _downloadedUrls.value.toMutableList()
                currentUrls.add(publicUrl)
                _downloadedUrls.value = currentUrls
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error uploading image: ${e.message}")
            }
        }
    }

    fun clearImageUrls() {
        _downloadedUrls.value = emptyList()
    }
}
