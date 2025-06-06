package com.example.yallabuy_user.data.remote

import android.util.Log
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException


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

    override suspend fun getAllProducts(): Flow<ProductResponse> {
        val products = service.getAllProducts()
        return flowOf(products)
    }

    override suspend fun getProductInfoById(productId: Long): Flow<ProductInfoResponse> {
        return try {
            val infoResponse = service.getProductById(productId)
            flowOf(infoResponse)
        }catch ( e : HttpException){
            Log.i("error", "getProductInfoById in remote http error ${e.message} ")
            flowOf()
        } catch (e : NullPointerException){
            Log.i("error", "getProductInfoById in remote null point  error ${e.message} ")
            flowOf()
        }
    }
    override suspend fun getPreviousOrders(userID: Long): Flow<OrdersResponse> {
        val orders = service.getPreviousOrders(userID)
        return flowOf(orders)
    }
}
