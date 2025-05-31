package com.example.yallabuy_user.collections


interface CollectionsViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getCategoryProducts(categoryID: Long)
    fun showSubCategoryProduct(subCategory : String)
}