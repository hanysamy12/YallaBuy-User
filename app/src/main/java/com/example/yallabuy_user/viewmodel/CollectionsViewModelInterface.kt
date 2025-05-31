package com.example.yallabuy_user.viewmodel

interface CollectionsViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getCategoryProducts(categoryID: Long)
    fun showSubCategoryProduct(subCategory : String)
}