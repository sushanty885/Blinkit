package com.example.userblinkitclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.userblinkitclone.databinding.ItemViewProductCategoryBinding
import com.example.userblinkitclone.models.Category

class AdapterCategory(
    private val categoryList: List<Category>,
    private val onCategoryClicked: (Category) -> Unit
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.ivCategory.setImageResource(category.icon)
        holder.binding.tvCategory.text = category.title
        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class CategoryViewHolder(val binding: ItemViewProductCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}