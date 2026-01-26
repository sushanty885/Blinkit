package com.example.userblinkitclone.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val quantity: Int,
    val unit: String,
    val price: Double,
    val stock: Int,
    val category: String,
    @SerialName("producttype")
    val productType: String,
    @SerialName("imageurls")
    val imageUrls: List<String>
)
