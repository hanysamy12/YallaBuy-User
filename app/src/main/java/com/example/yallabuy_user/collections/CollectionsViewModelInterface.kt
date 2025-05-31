package com.example.yallabuy_user.collections


interface CollectionsViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getProducts(categoryID: Long?)
    fun showSubCategoryProduct(subCategory : String)
    fun showFilteredProduct(minPrice : Double, maxPrice :Double)
}