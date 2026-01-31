package com.example.userblinkitclone.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProducts(cartProducts: CartProducts)

    @Update
    fun updateCartProducts(cartProducts: CartProducts)

    @Delete
    fun deleteCartProducts(cartProducts: CartProducts)

    @Query("DELETE FROM CartProducts")
    fun deleteAllCartProducts()

    @Query("SELECT * FROM CartProducts")
    fun getAllCartProducts(): Flow<List<CartProducts>>

    @Query("SELECT * FROM CartProducts WHERE id = :productId")
    fun getCartProduct(productId: Int): CartProducts?
}
