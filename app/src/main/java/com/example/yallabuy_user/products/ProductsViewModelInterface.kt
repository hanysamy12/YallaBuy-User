package com.example.yallabuy_user.products


interface ProductsViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getProducts(categoryID: Long?)
    fun showSubCategoryProduct(subCategory : String)
    fun showFilteredProduct(minPrice : Float, maxPrice :Float)
}