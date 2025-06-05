package com.example.yallabuy_user.products


interface ProductsViewModelInterface {
    suspend fun getAllCategories()
    suspend fun getProducts(vendorName: String?)
    suspend fun getCategoryProducts(categoryID: Long)
    fun showSubCategoryProduct(subCategory : String)
    fun showFilteredProduct(minPrice : Float, maxPrice :Float)
}