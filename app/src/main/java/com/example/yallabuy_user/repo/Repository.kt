package com.example.yallabuy_user.repo


import android.util.Log
import com.example.yallabuy_user.data.remote.RemoteDataSourceInterface
import com.example.yallabuy_user.data.models.BrandResponse
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.OrdersResponse
import com.example.yallabuy_user.data.models.ProductResponse
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.HttpException

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

    override suspend fun getProductById(productId: Long): Flow<ProductInfoResponse> {
        return try {
            val infoResponse = remoteDataSource.getProductInfoById(productId)
            infoResponse
        }catch ( e : HttpException){
            Log.i("error", "getProductInfoById in remote http error ${e.message} ")
            flowOf()
        } catch (e : NullPointerException){
            Log.i("error", "getProductInfoById in remote null point  error ${e.message} ")
            flowOf()
        }
    }
    override suspend fun getPreviousOrders(userID: Long): Flow<OrdersResponse> {
        return remoteDataSource.getPreviousOrders(userID)
    }
}