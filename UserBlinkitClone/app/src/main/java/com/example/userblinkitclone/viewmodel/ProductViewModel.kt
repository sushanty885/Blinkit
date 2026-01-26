package com.example.userblinkitclone.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems

    init {
        viewModelScope.launch {
            val savedCart = sharedPreferences.getString("cart", null)
            if (savedCart != null) {
                val type = object : TypeToken<List<Product>>() {}.type
                _cartItems.value = gson.fromJson(savedCart, type)
            }
        }
    }

    fun fetchProductsByCategory(category: String) {
        viewModelScope.launch {
            try {
                val client = Utils.getSupabase()

                val products = client
                    .from("Products")
                    .select {
                        filter {
                            eq("category", category)
                        }
                    }
                    .decodeList<Product>()

                _products.value = products
            } catch (e: Exception) {
                Log.e("SUPABASE", "Fetch failed", e)
            }
        }
    }

    fun fetchAllProducts() {
        viewModelScope.launch {
            try {
                val client = Utils.getSupabase()

                val products = client
                    .from("Products")
                    .select()
                    .decodeList<Product>()

                _products.value = products
            } catch (e: Exception) {
                Log.e("SUPABASE", "Fetch failed", e)
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                val client = Utils.getSupabase()

                val products = client
                    .from("Products")
                    .select {
                        filter {
                            ilike("title", "%${query}%")
                        }
                    }
                    .decodeList<Product>()

                _searchResults.value = products
            } catch (e: Exception) {
                Log.e("SUPABASE", "Search failed", e)
            }
        }
    }

    fun addToCart(product: Product) {
        val currentCartItems = _cartItems.value.toMutableList()
        currentCartItems.add(product)
        _cartItems.value = currentCartItems
        saveCart()
    }

    fun removeProduct(product: Product) {
        val currentCartItems = _cartItems.value.toMutableList()
        currentCartItems.remove(product)
        _cartItems.value = currentCartItems
        saveCart()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        saveCart()
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.size
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.price }
    }

    private fun saveCart() {
        val cartJson = gson.toJson(_cartItems.value)
        sharedPreferences.edit().putString("cart", cartJson).apply()
    }

    fun savingCartItemCount(itemCount: Int) {
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }
}