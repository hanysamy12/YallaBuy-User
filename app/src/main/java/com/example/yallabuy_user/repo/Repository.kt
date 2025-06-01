package com.example.yallabuy_user.repo


import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.ProductResponse
import kotlinx.coroutines.flow.Flow

class Repository(private val remoteDataSource: RemoteDataSourceInterface) : RepositoryInterface {
    override suspend fun getAllCategories(): Flow<CategoryResponse> {
        return  remoteDataSource.getAllCategories()
    }

    override suspend fun getAllBrands(): Flow<BrandResponse> {
        return remoteDataSource.getAllBrands()
    }

    override suspend fun getCategoryProducts(categoryID: Long): Flow<ProductResponse> {
        return remoteDataSource.getCategoryProducts(categoryID)
    }

    override suspend fun getAllProducts(): Flow<ProductResponse> {
        return remoteDataSource.getAllProducts()
    }
}