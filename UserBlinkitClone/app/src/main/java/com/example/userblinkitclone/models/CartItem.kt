package com.example.userblinkitclone.models

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val user_id: String,
    val product_id: String,
    val product_name: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
)
