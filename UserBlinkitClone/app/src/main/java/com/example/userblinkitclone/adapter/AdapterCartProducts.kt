package com.example.userblinkitclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.userblinkitclone.databinding.ItemCartProductBinding
import com.example.userblinkitclone.roomdb.CartProducts

class AdapterCartProducts : ListAdapter<CartProducts, AdapterCartProducts.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class CartViewHolder(private val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartProduct: CartProducts) {
            binding.apply {
                tvProductTitle.text = cartProduct.title
                tvProductQuantity.text = cartProduct.unit
                tvProductPrice.text = "₹${cartProduct.price?.toInt()}"
                ivProductImage.load(cartProduct.imageUrls)
                tvProductCount.text = cartProduct.quantity.toString()
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }
}
