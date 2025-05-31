package com.example.yallabuy_user.repo


import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.ProductResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<BrandResponse>
    suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse>
    suspend fun getAllProducts(): Flow<ProductResponse>

}