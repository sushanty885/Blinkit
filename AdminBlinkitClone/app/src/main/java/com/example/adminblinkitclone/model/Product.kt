package com.example.adminblinkitclone.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int? = null,
    val title: String? = null,
    val quantity: Int? = null,
    val unit: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val category: String? = null,
    @SerialName("producttype")
    val productType: String? = null,
    @SerialName("itemcount")
    val itemCount: Int? = null,
    @SerialName("adminid")
    val adminId: String? = null,
    val imageurls: List<String> = emptyList()
)
