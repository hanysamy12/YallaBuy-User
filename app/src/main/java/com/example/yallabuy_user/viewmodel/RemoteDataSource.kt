package com.example.yallabuy_user.viewmodel

import com.example.yallabuy_user.data.remote.ApiService
import com.example.yallabuy_user.models.BrandResponse
import com.example.yallabuy_user.models.CategoryResponse
import com.example.yallabuy_user.models.ProductResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class RemoteDataSource (private val service: ApiService) :
    RemoteDataSourceInterface {
    override suspend fun getAllCategories(): Flow<CategoryResponse> {
        val categories = service.getAllCategories()
        return flowOf(categories)
    }

    override suspend fun getAllBrands(): Flow<BrandResponse> {
        val brands = service.getAllBrands()
        return flowOf(brands)
    }

    override suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse> {
        val products = service.getCategoryProducts(categoryID)
        return flowOf(products)
    }

}
