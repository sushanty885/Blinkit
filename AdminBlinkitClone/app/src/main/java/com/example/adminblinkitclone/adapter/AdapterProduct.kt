package com.example.adminblinkitclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminblinkitclone.databinding.ItemViewProductBinding
import com.example.adminblinkitclone.model.Product

class AdapterProduct(
    val onEditClick: (Product) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {

    inner class ProductViewHolder(val binding: ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)
    var originalList: List<Product> = emptyList()
    private var productFilter: FilteringProducts? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()
            for (imageUrl in product.imageurls) {
                imageList.add(SlideModel(imageUrl))
            }
            ivImageSlider.setImageList(imageList)

            tvProductTitle.text = product.title
            val quantity = "${product.quantity}${product.unit}"
            tvProductQuantity.text = quantity
            tvProductPrice.text = "₹${product.price}"

            btnEdit.setOnClickListener {
                onEditClick(product)
            }
        }
    }

    override fun getFilter(): Filter {
        if (productFilter == null) {
            productFilter = FilteringProducts(this, originalList)
        }
        return productFilter!!
    }

}
