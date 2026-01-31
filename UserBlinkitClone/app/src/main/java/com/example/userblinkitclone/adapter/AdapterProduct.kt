package com.example.userblinkitclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.userblinkitclone.databinding.ItemProductBinding
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.roomdb.CartProducts

class AdapterProduct(
    private val onAddToCartClicked: (Product, ItemProductBinding) -> Unit,
    private val onIncrementClicked: (Product, ItemProductBinding) -> Unit,
    private val onDecrementClicked: (Product, ItemProductBinding) -> Unit,
) : ListAdapter<Product, AdapterProduct.ProductViewHolder>(ProductDiffCallback()) {

    private var cartProducts: List<CartProducts> = emptyList()

    fun setCartProducts(cartProducts: List<CartProducts>) {
        this.cartProducts = cartProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.apply {
            tvProductTitle.text = product.title
            tvProductQuantity.text = "${product.quantity} ${product.unit}"
            tvProductPrice.text = "₹${product.price.toInt()}"

            val imageList = ArrayList<SlideModel>()
            for (imageUrl in product.imageUrls) {
                imageList.add(SlideModel(imageUrl, ScaleTypes.CENTER_CROP))
            }
            ivImageSlider.setImageList(imageList)

            val cartProduct = cartProducts.find { it.id == product.id }
            if (cartProduct != null) {
                btnAdd.visibility = View.GONE
                llProductCount.visibility = View.VISIBLE
                tvProductCount.text = cartProduct.quantity.toString()
            } else {
                btnAdd.visibility = View.VISIBLE
                llProductCount.visibility = View.GONE
            }
        }

        holder.binding.btnAdd.setOnClickListener {
            onAddToCartClicked(product, holder.binding)
        }

        holder.binding.btnIncrement.setOnClickListener {
            onIncrementClicked(product, holder.binding)
        }

        holder.binding.btnDecrement.setOnClickListener {
            onDecrementClicked(product, holder.binding)
        }
    }

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
