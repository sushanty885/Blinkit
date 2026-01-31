package com.example.userblinkitclone.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "CartProducts")
data class CartProducts(
    @PrimaryKey
    val id: Int,
    val title: String? = null,
    var quantity: Int? = null,
    val unit: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val category: String? = null,
    val productType: String? = null,
    val imageUrls: String? = null
)
