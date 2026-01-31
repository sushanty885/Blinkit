package com.example.userblinkitclone.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProducts::class], version = 1, exportSchema = false)
abstract class CartProductsDatabase : RoomDatabase() {

    abstract fun cartProductsDao(): CartProductsDao

    companion object {
        @Volatile
        private var INSTANCE: CartProductsDatabase? = null

        fun getDatabase(context: Context): CartProductsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartProductsDatabase::class.java,
                    "CartProductsDatabase"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
