package com.example.adminblinkitclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminblinkitclone.databinding.ItemCategoryBinding
import com.example.adminblinkitclone.model.Categories

class CategoriesAdapter(
    private val categoryList: ArrayList<Categories>,
    private val onCategoryClicked: (Categories) -> Unit
) :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
            ivCategory.setImageResource(category.icon)
            tvCategoryTitle.text = category.title
        }
        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class CategoriesViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}
