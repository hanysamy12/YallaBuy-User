package com.example.yallabuy_user.repo


import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<BrandResponse>
    suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse>
    suspend fun getAllProducts(): Flow<ProductResponse>
    suspend fun getProductById(productId : Long) : Flow<ProductInfoResponse>
    suspend fun createUserAccount(email: String, password: String): String
    suspend fun loginUser(email : String , password : String) : Boolean
}