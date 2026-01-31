package com.example.userblinkitclone.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderSupabaseDto(
    @SerialName("order_id")
    val orderId: String,

    @SerialName("order_list")
    val orderList: String,

    @SerialName("user_address")
    val userAddress: String,

    @SerialName("order_status")
    val orderStatus: Int,

    @SerialName("order_date")
    val orderDate: String,

    @SerialName("ordering_user_id")
    val orderingUserId: String
)
