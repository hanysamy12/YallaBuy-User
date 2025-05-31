package com.example.yallabuy_user.repo


import com.example.yallabuy_user.models.BrandResponse
import com.example.yallabuy_user.models.CategoryResponse
import com.example.yallabuy_user.models.ProductResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<BrandResponse>
    suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse>
}