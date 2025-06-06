package com.example.yallabuy_user.data.remote


import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSourceInterface {
    suspend fun getAllCategories(): Flow<CategoryResponse>
    suspend fun getAllBrands(): Flow<BrandResponse>
    suspend fun getCategoryProducts(categoryID :Long): Flow<ProductResponse>
    suspend fun getAllProducts(): Flow<ProductResponse>
    suspend fun getProductInfoById(productId : Long) : Flow<ProductInfoResponse>
    suspend fun getPreviousOrders(userID : Long) : Flow<OrdersResponse>

}