package com.example.userblinkitclone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.userblinkitclone.databinding.ItemProductBinding
import com.example.userblinkitclone.model.Product

class AdapterProduct(
    private val productList: List<Product>,
    private val onAddToCartClicked: (Product, ItemProductBinding) -> Unit,
    private val onIncrementClicked: (Product, ItemProductBinding) -> Unit,
    private val onDecrementClicked: (Product, ItemProductBinding) -> Unit,
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.apply {
            tvProductTitle.text = product.title
            tvProductQuantity.text = "${product.quantity} ${product.unit}"
            tvProductPrice.text = "₹${String.format("%.0f", product.price)}"

            val imageList = ArrayList<SlideModel>()
            for (imageUrl in product.imageUrls) {
                imageList.add(SlideModel(imageUrl, ScaleTypes.CENTER_CROP))
            }
            ivImageSlider.setImageList(imageList)
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

    override fun getItemCount() = productList.size

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}
