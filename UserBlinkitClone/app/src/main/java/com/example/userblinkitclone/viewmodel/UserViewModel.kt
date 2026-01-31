package com.example.userblinkitclone.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.models.CartItem
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.models.Users
import com.example.userblinkitclone.roomdb.CartProducts
import com.example.userblinkitclone.roomdb.CartProductsDatabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
    private val cartProductsDao = CartProductsDatabase.getDatabase(application).cartProductsDao()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults

    private val _cartItems = MutableStateFlow<List<CartProducts>>(emptyList())
    val cartItems: StateFlow<List<CartProducts>> = _cartItems.asStateFlow()

    init {
        viewModelScope.launch {
            cartProductsDao.getAllCartProducts().collect {
                _cartItems.value = it
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
        viewModelScope.launch(Dispatchers.IO) {
            val cartProduct = cartProductsDao.getCartProduct(product.id)
            val newQuantity: Int
            if (cartProduct == null) {
                newQuantity = 1
                cartProductsDao.insertCartProducts(
                    CartProducts(
                        id = product.id,
                        title = product.title,
                        quantity = newQuantity,
                        unit = product.unit,
                        price = product.price,
                        stock = product.stock,
                        category = product.category,
                        productType = product.productType,
                        imageUrls = product.imageUrls.firstOrNull()
                    )
                )
            } else {
                newQuantity = (cartProduct.quantity ?: 0) + 1
                cartProduct.quantity = newQuantity
                cartProductsDao.updateCartProducts(cartProduct)
            }
            upsertCartItemInSupabase(product, newQuantity)
        }
    }

    fun removeProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val cartProduct = cartProductsDao.getCartProduct(product.id)
            if (cartProduct != null) {
                if (cartProduct.quantity!! > 1) {
                    val newQuantity = cartProduct.quantity!! - 1
                    cartProduct.quantity = newQuantity
                    cartProductsDao.updateCartProducts(cartProduct)
                    upsertCartItemInSupabase(product, newQuantity)
                } else {
                    cartProductsDao.deleteCartProducts(cartProduct)
                    deleteCartItemFromSupabase(product)
                }
            }
        }
    }

    private fun upsertCartItemInSupabase(product: Product, quantity: Int) {
        viewModelScope.launch {
            try {
                val userId = Utils.getSupabase().auth.currentUserOrNull()?.id ?: return@launch
                val existingItem = Utils.getSupabase().from("cart_items")
                    .select {
                        filter {
                            eq("user_id", userId)
                            eq("product_id", product.id.toString())
                        }
                        limit(1)
                    }.decodeSingleOrNull<CartItem>()

                if (existingItem == null) {
                    val newItem = CartItem(
                        user_id = userId,
                        product_id = product.id.toString(),
                        product_name = product.title,
                        price = product.price,
                        quantity = quantity
                    )
                    Utils.getSupabase().from("cart_items").insert(newItem)
                } else {
                    Utils.getSupabase().from("cart_items").update(
                        { set("quantity", quantity) }
                    ) {
                        filter {
                            eq("user_id", userId)
                            eq("product_id", product.id.toString())
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error upserting cart item in Supabase", e)
            }
        }
    }

    private fun deleteCartItemFromSupabase(product: Product) {
        viewModelScope.launch {
            try {
                val userId = Utils.getSupabase().auth.currentUserOrNull()?.id ?: return@launch
                Utils.getSupabase().from("cart_items").delete {
                    filter {
                        eq("user_id", userId)
                        eq("product_id", product.id.toString())
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error deleting cart item from Supabase", e)
            }
        }
    }


    fun getCartItemCount(): Int {
        return _cartItems.value.size
    }

    fun savingCartItemCount(itemCount: Int) {
        sharedPreferences.edit { putInt("itemCount", itemCount) }
    }

    fun getAllCartProducts() = cartProductsDao.getAllCartProducts()

    fun saveAddress(users: Users) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("address", "${users.userAddress}, ${users.userDistrict}, ${users.userState}, ${users.userPinCode}").apply()
            Utils.getSupabase().from("users").insert(users)
        }
    }

    fun getAddress(): String? {
        return sharedPreferences.getString("address", null)
    }

    fun saveAddressStatus() {
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    fun getAddressStatus(): MutableLiveData<Boolean> {
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus", false)
        return status
    }

    fun deleteAllProductsFromCart() {
        viewModelScope.launch(Dispatchers.IO) {
            cartProductsDao.deleteAllCartProducts()
            clearSupabaseCart()
        }
    }

    private fun clearSupabaseCart() {
        viewModelScope.launch {
            try {
                val userId = Utils.getSupabase().auth.currentUserOrNull()?.id ?: return@launch
                Utils.getSupabase().from("cart_items").delete {
                    filter {
                        eq("user_id", userId)
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error clearing Supabase cart", e)
            }
        }
    }
}
