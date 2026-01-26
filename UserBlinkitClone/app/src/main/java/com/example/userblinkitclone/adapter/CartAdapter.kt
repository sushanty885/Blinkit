package com.example.userblinkitclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.userblinkitclone.databinding.ItemCartProductBinding
import com.example.userblinkitclone.model.Product

class CartAdapter(private val cartItems: List<Product>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartItems[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvProductTitle.text = product.title
            binding.tvProductQuantity.text = "${product.quantity} ${product.unit}"
            binding.tvProductPrice.text = "₹${product.price}"

            // Set the product image if you have one
            // binding.ivProductImage.setImageResource(product.productImage)
        }
    }
}