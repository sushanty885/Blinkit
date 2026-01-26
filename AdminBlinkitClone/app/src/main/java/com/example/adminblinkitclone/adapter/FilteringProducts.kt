package com.example.adminblinkitclone.adapter

import android.widget.Filter
import com.example.adminblinkitclone.model.Product
import java.util.Locale

class FilteringProducts(
    private val adapter: AdapterProduct,
    private val filterList: List<Product>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        if (!constraint.isNullOrEmpty()) {
            val queryParts = constraint.toString().trim().lowercase(Locale.getDefault()).split(" ")
            val filteredProducts = filterList.filter { product ->
                queryParts.any { queryPart ->
                    product.title?.lowercase(Locale.getDefault())?.contains(queryPart) == true ||
                            product.category?.lowercase(Locale.getDefault())?.contains(queryPart) == true ||
                            product.productType?.lowercase(Locale.getDefault())?.contains(queryPart) == true
                }
            }
            results.values = filteredProducts
            results.count = filteredProducts.size
        } else {
            results.values = filterList
            results.count = filterList.size
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as? List<Product>)
    }
}
