package com.example.userblinkitclone.repository

import android.util.Log
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.models.OrderSupabaseDto
import com.example.userblinkitclone.roomdb.CartProducts
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrderRepository {

    private suspend fun getSupabase() = Utils.getSupabase()

    suspend fun saveOrder(
        cartList: List<CartProducts>,
        address: String
    ): String {

        Log.d("SUPABASE_USER", getSupabase().auth.currentUserOrNull()?.id ?: "NULL USER")
        val userId = getSupabase().auth.currentUserOrNull()?.id
            ?: throw Exception("User not logged in")

        val orderId = UUID.randomUUID().toString()

        val cartJson = Json.encodeToString(cartList)

        val orderDto = OrderSupabaseDto(
            orderId = orderId,
            orderList = cartJson,
            userAddress = address,
            orderStatus = 0,
            orderDate = SimpleDateFormat(
                "dd-MM-yyyy HH:mm",
                Locale.getDefault()
            ).format(Date()),
            orderingUserId = userId
        )

        getSupabase().from("orders").insert(orderDto)

        return orderId
    }

    suspend fun markOrderPaid(orderId: String) {
        getSupabase().from("orders")
            .update(mapOf("order_status" to 1)) {
                filter {
                    eq("order_id", orderId)
                }
            }
    }

    suspend fun getMyOrders(): List<OrderSupabaseDto> {
        return getSupabase().from("orders")
            .select(){
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList()
    }
}
